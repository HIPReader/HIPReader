package com.example.hipreader.domain.bookscore.entity;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QYearlyTopBook is a Querydsl query type for YearlyTopBook
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QYearlyTopBook extends EntityPathBase<YearlyTopBook> {

    private static final long serialVersionUID = 145755435L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QYearlyTopBook yearlyTopBook = new QYearlyTopBook("yearlyTopBook");

    public final com.example.hipreader.domain.book.entity.QBook book;

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final NumberPath<Long> totalScore = createNumber("totalScore", Long.class);

    public final NumberPath<Integer> year = createNumber("year", Integer.class);

    public QYearlyTopBook(String variable) {
        this(YearlyTopBook.class, forVariable(variable), INITS);
    }

    public QYearlyTopBook(Path<? extends YearlyTopBook> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QYearlyTopBook(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QYearlyTopBook(PathMetadata metadata, PathInits inits) {
        this(YearlyTopBook.class, metadata, inits);
    }

    public QYearlyTopBook(Class<? extends YearlyTopBook> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.book = inits.isInitialized("book") ? new com.example.hipreader.domain.book.entity.QBook(forProperty("book")) : null;
    }

}

