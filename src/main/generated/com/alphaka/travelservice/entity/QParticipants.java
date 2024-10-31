package com.alphaka.travelservice.entity;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;


/**
 * QParticipants is a Querydsl query type for Participants
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QParticipants extends EntityPathBase<Participants> {

    private static final long serialVersionUID = -1362696335L;

    public static final QParticipants participants = new QParticipants("participants");

    public final DateTimePath<java.time.LocalDateTime> joinedAt = createDateTime("joinedAt", java.time.LocalDateTime.class);

    public final NumberPath<Long> participantId = createNumber("participantId", Long.class);

    public final EnumPath<Permission> permission = createEnum("permission", Permission.class);

    public final NumberPath<Long> travelId = createNumber("travelId", Long.class);

    public final NumberPath<Long> userId2 = createNumber("userId2", Long.class);

    public QParticipants(String variable) {
        super(Participants.class, forVariable(variable));
    }

    public QParticipants(Path<? extends Participants> path) {
        super(path.getType(), path.getMetadata());
    }

    public QParticipants(PathMetadata metadata) {
        super(Participants.class, metadata);
    }

}

