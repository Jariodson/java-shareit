package ru.practicum.shareit.item.mapper;

import org.springframework.stereotype.Component;
import ru.practicum.shareit.item.dto.CommentCreatedDto;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.model.Comment;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class CommentMapper {
    public CommentDto transformCommentToCommentDto(Comment comment){
        return CommentDto.builder()
                .id(comment.getId())
                .text(comment.getText())
                .authorName(comment.getAuthor().getName())
                .created(comment.getCreated())
                .build();
    }

    public Comment transformCommentCreatedDtoToComment(CommentCreatedDto commentCreatedDto){
        return Comment.builder()
                .text(commentCreatedDto.getText())
                .created(LocalDateTime.now())
                .build();
    }

    public List<CommentDto> transformCommentsListToCommentsDtoList(List<Comment> comments){
        return comments.stream().map(this::transformCommentToCommentDto).collect(Collectors.toList());
    }
}
