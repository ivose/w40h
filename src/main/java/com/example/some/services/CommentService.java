package com.example.some.services;

import com.example.some.dto.comments.*;
import com.example.some.entities.Comment;
import com.example.some.entities.Post;
import com.example.some.entities.User;
import com.example.some.models.search.SearchCriteriaModel;
import com.example.some.repositories.CommentRepository;
import com.example.some.repositories.PostRepository;
import com.example.some.repositories.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

@Service
@Transactional
public class CommentService {
    private final CommentRepository commentRepository;
    private final PostRepository postRepository;
    private final UserRepository userRepository;

    public CommentService(CommentRepository commentRepository,
                          PostRepository postRepository,
                          UserRepository userRepository) {
        this.commentRepository = commentRepository;
        this.postRepository = postRepository;
        this.userRepository = userRepository;
    }

    public Page<CommentResponseDTO> getUserComments(Long userId, Pageable pageable) {
        return commentRepository.findUserComments(userId, pageable)
                .map(this::convertToCommentResponse);
    }

    public Page<CommentResponseDTO> findAllCommentsByPostId(Long postId, Pageable pageable) {
        return commentRepository.findAllCommentsByPostId(postId, pageable)
                .map(this::convertToCommentResponse);
    }

    public CommentResponseDTO createComment(Long userId, CommentCreateRequestDTO request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Post post = postRepository.findById(request.getPostId())
                .orElseThrow(() -> new RuntimeException("Post not found"));

        Comment comment = new Comment();
        comment.setUser(user);
        comment.setPost(post);
        comment.setContent(request.getContent());

        // Handle reply if parentCommentId is provided and not 0
        if (request.getParentCommentId() != null && request.getParentCommentId() > 0) {
            Comment parentComment = commentRepository.findById(request.getParentCommentId())
                    .orElseThrow(() -> new RuntimeException("Parent comment not found"));
            if (!parentComment.getPost().getId().equals(request.getPostId())) {
                throw new RuntimeException("Reply must be for the same post as parent comment");
            }
            comment.setParentComment(parentComment);
        }

        Comment savedComment = commentRepository.save(comment);
        return convertToCommentResponse(savedComment);
    }


    public Page<CommentResponseDTO> getPostComments(Long postId, Pageable pageable) {
        //return commentRepository.findByPostId(postId, pageable)
        //        .map(this::convertToCommentResponse);
        return commentRepository.findTopLevelCommentsByPostId(postId, pageable)
                .map(comment -> {
                    CommentReplyResponseDTO response = convertToCommentReplyResponse(comment);
                    return response;
                });
    }

    private CommentResponseDTO convertToCommentResponse(Comment comment) {
        CommentResponseDTO response = new CommentResponseDTO();
        response.setId(comment.getId());
        response.setUserId(comment.getUser().getId());
        response.setUsername(comment.getUser().getUsername());
        response.setContent(comment.getContent());
        response.setCreatedAt(comment.getCreatedAt());
        return response;
    }



    //only for admin
    public void deleteComment(Long commentId) {
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new RuntimeException("Comment not found"));
        commentRepository.deleteById(commentId);
    }

    public CommentResponseDTO updateComment(Long commentId, CommentUpdateRequestDTO request) {
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new RuntimeException("Comment not found"));
        comment.setContent(request.getContent());
        return convertToCommentResponse(commentRepository.save(comment));
    }

    public CommentResponseDTO getComment(Long commentId) {
        return commentRepository.findById(commentId)
                .map(this::convertToCommentResponse)
                .orElseThrow(() -> new RuntimeException("Comment not found"));
    }

    public Page<CommentResponseDTO> searchComments(SearchCriteriaModel criteria, Pageable pageable) {
        if (criteria != null && criteria.getSearchTerm() != null && !criteria.getSearchTerm().trim().isEmpty()) {
            return commentRepository.searchComments(criteria.getSearchTerm().trim(), pageable)
                    .map(this::convertToCommentResponse);
        }
        if (criteria != null && criteria.getUserIds() != null && !criteria.getUserIds().isEmpty()) {
            return commentRepository.findAll(pageable)
                    .map(this::convertToCommentResponse);
        }
        return commentRepository.findAll(pageable)
                .map(this::convertToCommentResponse);
    }


    public CommentReplyResponseDTO createReply(Long userId, CommentReplyRequestDTO request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Post post = postRepository.findById(request.getPostId())
                .orElseThrow(() -> new RuntimeException("Post not found"));

        Comment parentComment = commentRepository.findById(request.getParentCommentId())
                .orElseThrow(() -> new RuntimeException("Parent comment not found"));

        Comment reply = new Comment();
        reply.setUser(user);
        reply.setPost(post);
        reply.setParentComment(parentComment);
        reply.setContent(request.getContent());

        Comment savedReply = commentRepository.save(reply);
        return convertToCommentReplyResponse(savedReply);
    }


    public Page<CommentReplyResponseDTO> getCommentReplies(Long commentId, Pageable pageable) {
        return commentRepository.findByParentCommentId(commentId, pageable)
                .map(this::convertToCommentReplyResponse);
    }


    private CommentReplyResponseDTO convertToCommentReplyResponse(Comment comment) {
        CommentReplyResponseDTO response = new CommentReplyResponseDTO();
        response.setId(comment.getId());
        response.setUserId(comment.getUser().getId());
        response.setUsername(comment.getUser().getUsername());
        response.setContent(comment.getContent());
        response.setCreatedAt(comment.getCreatedAt());

        if (comment.getParentComment() != null) {
            response.setParentCommentId(comment.getParentComment().getId());
        }

        response.setRepliesCount((int) commentRepository.countRepliesByCommentId(comment.getId()));
        return response;
    }

    public CommentDetailResponseDTO getCommentWithReplies(Long commentId) {
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new RuntimeException("Comment not found"));
        return convertToDetailResponse(comment);
    }

    private CommentDetailResponseDTO convertToDetailResponse(Comment comment) {
        CommentDetailResponseDTO response = new CommentDetailResponseDTO();
        response.setId(comment.getId());
        response.setUserId(comment.getUser().getId());
        response.setUsername(comment.getUser().getUsername());
        response.setContent(comment.getContent());
        response.setCreatedAt(comment.getCreatedAt());

        for (Comment reply : comment.getReplies()) {
            response.getChildren().add(convertToDetailResponse(reply));
        }

        return response;
    }

    public CommentResponseDTO updateOwnComment(Long userId, Long commentId, CommentUpdateRequestDTO request) {
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new RuntimeException("Comment not found"));

        if (!comment.getUser().getId().equals(userId)) {
            throw new RuntimeException("Not authorized to update this comment");
        }

        comment.setContent(request.getContent());
        return convertToCommentResponse(commentRepository.save(comment));
    }

    public void deleteOwnComment(Long userId, Long commentId) {
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new RuntimeException("Comment not found"));

        if (!comment.getUser().getId().equals(userId)) {
            throw new RuntimeException("Not authorized to delete this comment");
        }

        commentRepository.delete(comment);
    }



}