package com.example.hipreader.domain.book.entity;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QBook is a Querydsl query type for Book
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QBook extends EntityPathBase<Book> {

    private static final long serialVersionUID = -96898548L;

    public static final QBook book = new QBook("book");

    public final com.example.hipreader.common.entity.QTimeStamped _super = new com.example.hipreader.common.entity.QTimeStamped(this);

    public final ListPath<Author, QAuthor> authors = this.<Author, QAuthor>createList("authors", Author.class, QAuthor.class, PathInits.DIRECT2);

    public final StringPath categoryName = createString("categoryName");

    public final StringPath coverImage = createString("coverImage");

    //inherited
    public final DateTimePath<java.time.LocalDateTime> createdAt = _super.createdAt;

    public final DatePath<java.time.LocalDate> datePublished = createDate("datePublished", java.time.LocalDate.class);

    //inherited
    public final DateTimePath<java.time.LocalDateTime> deletedAt = _super.deletedAt;

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final StringPath isbn13 = createString("isbn13");

    public final StringPath publisher = createString("publisher");

    public final StringPath title = createString("title");

    public final NumberPath<Integer> totalPages = createNumber("totalPages", Integer.class);

    //inherited
    public final DateTimePath<java.time.LocalDateTime> updatedAt = _super.updatedAt;

    public QBook(String variable) {
        super(Book.class, forVariable(variable));
    }

    public QBook(Path<? extends Book> path) {
        super(path.getType(), path.getMetadata());
    }

    public QBook(PathMetadata metadata) {
        super(Book.class, metadata);
    }

}

