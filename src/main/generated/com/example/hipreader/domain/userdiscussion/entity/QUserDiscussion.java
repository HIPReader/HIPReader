package com.example.hipreader.domain.userdiscussion.entity;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QUserDiscussion is a Querydsl query type for UserDiscussion
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QUserDiscussion extends EntityPathBase<UserDiscussion> {

    private static final long serialVersionUID = -1003202240L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QUserDiscussion userDiscussion = new QUserDiscussion("userDiscussion");

    public final com.example.hipreader.common.entity.QTimeStamped _super = new com.example.hipreader.common.entity.QTimeStamped(this);

    public final DateTimePath<java.time.LocalDateTime> appliedAt = createDateTime("appliedAt", java.time.LocalDateTime.class);

    //inherited
    public final DateTimePath<java.time.LocalDateTime> createdAt = _super.createdAt;

    //inherited
    public final DateTimePath<java.time.LocalDateTime> deletedAt = _super.deletedAt;

    public final com.example.hipreader.domain.discussion.entity.QDiscussion discussion;

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final EnumPath<com.example.hipreader.domain.userdiscussion.applicationStatus.ApplicationStatus> status = createEnum("status", com.example.hipreader.domain.userdiscussion.applicationStatus.ApplicationStatus.class);

    public final DateTimePath<java.time.LocalDateTime> statusUpdatedAt = createDateTime("statusUpdatedAt", java.time.LocalDateTime.class);

    //inherited
    public final DateTimePath<java.time.LocalDateTime> updatedAt = _super.updatedAt;

    public final com.example.hipreader.domain.user.entity.QUser user;

    public QUserDiscussion(String variable) {
        this(UserDiscussion.class, forVariable(variable), INITS);
    }

    public QUserDiscussion(Path<? extends UserDiscussion> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QUserDiscussion(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QUserDiscussion(PathMetadata metadata, PathInits inits) {
        this(UserDiscussion.class, metadata, inits);
    }

    public QUserDiscussion(Class<? extends UserDiscussion> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.discussion = inits.isInitialized("discussion") ? new com.example.hipreader.domain.discussion.entity.QDiscussion(forProperty("discussion"), inits.get("discussion")) : null;
        this.user = inits.isInitialized("user") ? new com.example.hipreader.domain.user.entity.QUser(forProperty("user")) : null;
    }

}

