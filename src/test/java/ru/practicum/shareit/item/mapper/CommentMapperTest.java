package ru.practicum.shareit.item.mapper;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.practicum.shareit.item.dto.CommentCreatedDto;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

public class CommentMapperTest {

    private CommentMapper commentMapper;

    @BeforeEach
    public void setUp() {
        commentMapper = new CommentMapper();
    }

    @Test
    public void testTransformCommentToCommentDto() {
        User author = User.builder().id(1L).name("Author Name").build();
        Comment comment = Comment.builder()
                .id(1L)
                .text("Test Comment")
                .author(author)
                .created(LocalDateTime.now())
                .build();

        CommentDto commentDto = commentMapper.transformCommentToCommentDto(comment);

        assertThat(commentDto.getId()).isEqualTo(1L);
        assertThat(commentDto.getText()).isEqualTo("Test Comment");
        assertThat(commentDto.getAuthorName()).isEqualTo("Author Name");
        assertThat(commentDto.getCreated()).isEqualTo(comment.getCreated());
    }

    @Test
    public void testTransformCommentCreatedDtoToComment() {
        CommentCreatedDto commentCreatedDto = new CommentCreatedDto();
        commentCreatedDto.setText("New Comment");

        Comment comment = commentMapper.transformCommentCreatedDtoToComment(commentCreatedDto);

        assertThat(comment.getText()).isEqualTo("New Comment");
        assertThat(comment.getCreated()).isNotNull();
    }

    @Test
    public void testTransformCommentsListToCommentsDtoList() {
        User author1 = User.builder().id(1L).name("Author1").build();
        User author2 = User.builder().id(2L).name("Author2").build();
        Comment comment1 = Comment.builder()
                .id(1L)
                .text("Comment1")
                .author(author1)
                .created(LocalDateTime.now())
                .build();
        Comment comment2 = Comment.builder()
                .id(2L)
                .text("Comment2")
                .author(author2)
                .created(LocalDateTime.now())
                .build();

        List<Comment> comments = List.of(comment1, comment2);

        List<CommentDto> commentDtos = commentMapper.transformCommentsListToCommentsDtoList(comments);

        assertThat(commentDtos).hasSize(2);
        assertThat(commentDtos.get(0).getId()).isEqualTo(1L);
        assertThat(commentDtos.get(0).getText()).isEqualTo("Comment1");
        assertThat(commentDtos.get(0).getAuthorName()).isEqualTo("Author1");
        assertThat(commentDtos.get(0).getCreated()).isEqualTo(comment1.getCreated());
        assertThat(commentDtos.get(1).getId()).isEqualTo(2L);
        assertThat(commentDtos.get(1).getText()).isEqualTo("Comment2");
        assertThat(commentDtos.get(1).getAuthorName()).isEqualTo("Author2");
        assertThat(commentDtos.get(1).getCreated()).isEqualTo(comment2.getCreated());
    }
}
