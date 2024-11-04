package com.alphaka.travelservice.entity;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QInvitations is a Querydsl query type for Invitations
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QInvitations extends EntityPathBase<Invitations> {

    private static final long serialVersionUID = 1884474889L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QInvitations invitations = new QInvitations("invitations");

    public final DateTimePath<java.time.LocalDateTime> createdAt = createDateTime("createdAt", java.time.LocalDateTime.class);

    public final NumberPath<Long> invitationId = createNumber("invitationId", Long.class);

    public final StringPath invitationMessage = createString("invitationMessage");

    public final EnumPath<InvitationStatus> status = createEnum("status", InvitationStatus.class);

    public final QTravelPlans travelPlans;

    public final DateTimePath<java.time.LocalDateTime> updatedAt = createDateTime("updatedAt", java.time.LocalDateTime.class);

    public final NumberPath<Long> userId = createNumber("userId", Long.class);

    public QInvitations(String variable) {
        this(Invitations.class, forVariable(variable), INITS);
    }

    public QInvitations(Path<? extends Invitations> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QInvitations(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QInvitations(PathMetadata metadata, PathInits inits) {
        this(Invitations.class, metadata, inits);
    }

    public QInvitations(Class<? extends Invitations> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.travelPlans = inits.isInitialized("travelPlans") ? new QTravelPlans(forProperty("travelPlans")) : null;
    }

}

