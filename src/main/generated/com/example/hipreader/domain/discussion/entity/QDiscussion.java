package com.example.hipreader.domain.discussion.entity;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QDiscussion is a Querydsl query type for Discussion
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QDiscussion extends EntityPathBase<Discussion> {

    private static final long serialVersionUID = 1759947466L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QDiscussion discussion = new QDiscussion("discussion");

    public final com.example.hipreader.common.entity.QTimeStamped _super = new com.example.hipreader.common.entity.QTimeStamped(this);

    public final com.example.hipreader.domain.book.entity.QBook book;

    //inherited
    public final DateTimePath<java.time.LocalDateTime> createdAt = _super.createdAt;

    //inherited
    public final DateTimePath<java.time.LocalDateTime> deletedAt = _super.deletedAt;

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final EnumPath<com.example.hipreader.domain.userdiscussion.status.DiscussionMode> mode = createEnum("mode", com.example.hipreader.domain.userdiscussion.status.DiscussionMode.class);

    public final NumberPath<Integer> participants = createNumber("participants", Integer.class);

    public final DateTimePath<java.time.LocalDateTime> scheduledAt = createDateTime("scheduledAt", java.time.LocalDateTime.class);

    public final EnumPath<com.example.hipreader.domain.discussion.status.Status> status = createEnum("status", com.example.hipreader.domain.discussion.status.Status.class);

    public final StringPath topic = createString("topic");

    //inherited
    public final DateTimePath<java.time.LocalDateTime> updatedAt = _super.updatedAt;

    public final com.example.hipreader.domain.user.entity.QUser user;

    public final NumberPath<Long> version = createNumber("version", Long.class);

    public QDiscussion(String variable) {
        this(Discussion.class, forVariable(variable), INITS);
    }

    public QDiscussion(Path<? extends Discussion> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QDiscussion(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QDiscussion(PathMetadata metadata, PathInits inits) {
        this(Discussion.class, metadata, inits);
    }

    public QDiscussion(Class<? extends Discussion> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.book = inits.isInitialized("book") ? new com.example.hipreader.domain.book.entity.QBook(forProperty("book")) : null;
        this.user = inits.isInitialized("user") ? new com.example.hipreader.domain.user.entity.QUser(forProperty("user")) : null;
    }

}

