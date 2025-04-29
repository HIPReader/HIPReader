package com.example.hipreader.domain.userbook.entity;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QUserBook is a Querydsl query type for UserBook
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QUserBook extends EntityPathBase<UserBook> {

    private static final long serialVersionUID = -1750004542L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QUserBook userBook = new QUserBook("userBook");

    public final com.example.hipreader.common.entity.QTimeStamped _super = new com.example.hipreader.common.entity.QTimeStamped(this);

    public final com.example.hipreader.domain.book.entity.QBook book;

    //inherited
    public final DateTimePath<java.time.LocalDateTime> createdAt = _super.createdAt;

    //inherited
    public final DateTimePath<java.time.LocalDateTime> deletedAt = _super.deletedAt;

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final NumberPath<Integer> progress = createNumber("progress", Integer.class);

    public final EnumPath<com.example.hipreader.domain.userbook.status.Status> status = createEnum("status", com.example.hipreader.domain.userbook.status.Status.class);

    //inherited
    public final DateTimePath<java.time.LocalDateTime> updatedAt = _super.updatedAt;

    public final com.example.hipreader.domain.user.entity.QUser user;

    public QUserBook(String variable) {
        this(UserBook.class, forVariable(variable), INITS);
    }

    public QUserBook(Path<? extends UserBook> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QUserBook(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QUserBook(PathMetadata metadata, PathInits inits) {
        this(UserBook.class, metadata, inits);
    }

    public QUserBook(Class<? extends UserBook> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.book = inits.isInitialized("book") ? new com.example.hipreader.domain.book.entity.QBook(forProperty("book")) : null;
        this.user = inits.isInitialized("user") ? new com.example.hipreader.domain.user.entity.QUser(forProperty("user")) : null;
    }

}

