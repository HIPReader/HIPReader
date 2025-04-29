package com.example.hipreader.domain.bookscore.entity;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QBookScore is a Querydsl query type for BookScore
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QBookScore extends EntityPathBase<BookScore> {

    private static final long serialVersionUID = -351669792L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QBookScore bookScore = new QBookScore("bookScore");

    public final com.example.hipreader.domain.book.entity.QBook book;

    public final NumberPath<Long> finished = createNumber("finished", Long.class);

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final NumberPath<Long> reading = createNumber("reading", Long.class);

    public final NumberPath<Long> toRead = createNumber("toRead", Long.class);

    public QBookScore(String variable) {
        this(BookScore.class, forVariable(variable), INITS);
    }

    public QBookScore(Path<? extends BookScore> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QBookScore(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QBookScore(PathMetadata metadata, PathInits inits) {
        this(BookScore.class, metadata, inits);
    }

    public QBookScore(Class<? extends BookScore> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.book = inits.isInitialized("book") ? new com.example.hipreader.domain.book.entity.QBook(forProperty("book")) : null;
    }

}

