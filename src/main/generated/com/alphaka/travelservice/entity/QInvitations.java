package com.alphaka.travelservice.entity;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;


/**
 * QInvitations is a Querydsl query type for Invitations
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QInvitations extends EntityPathBase<Invitations> {

    private static final long serialVersionUID = 1884474889L;

    public static final QInvitations invitations = new QInvitations("invitations");

    public final DateTimePath<java.time.LocalDateTime> createdAt = createDateTime("createdAt", java.time.LocalDateTime.class);

    public final NumberPath<Long> invitationId = createNumber("invitationId", Long.class);

    public final StringPath invitationMessage = createString("invitationMessage");

    public final EnumPath<InvitationStatus> status = createEnum("status", InvitationStatus.class);

    public final NumberPath<Long> travelId = createNumber("travelId", Long.class);

    public final NumberPath<Long> travelId2 = createNumber("travelId2", Long.class);

    public final DateTimePath<java.time.LocalDateTime> updatedAt = createDateTime("updatedAt", java.time.LocalDateTime.class);

    public QInvitations(String variable) {
        super(Invitations.class, forVariable(variable));
    }

    public QInvitations(Path<? extends Invitations> path) {
        super(path.getType(), path.getMetadata());
    }

    public QInvitations(PathMetadata metadata) {
        super(Invitations.class, metadata);
    }

}

