package com.alphaka.travelservice.entity;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QTravelPlans is a Querydsl query type for TravelPlans
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QTravelPlans extends EntityPathBase<TravelPlans> {

    private static final long serialVersionUID = -1150542465L;

    public static final QTravelPlans travelPlans = new QTravelPlans("travelPlans");

    public final DateTimePath<java.time.LocalDateTime> createdAt = createDateTime("createdAt", java.time.LocalDateTime.class);

    public final DateTimePath<java.time.LocalDateTime> deletedAt = createDateTime("deletedAt", java.time.LocalDateTime.class);

    public final StringPath description = createString("description");

    public final DatePath<java.time.LocalDate> endDate = createDate("endDate", java.time.LocalDate.class);

    public final ListPath<Invitations, QInvitations> invitations = this.<Invitations, QInvitations>createList("invitations", Invitations.class, QInvitations.class, PathInits.DIRECT2);

    public final StringPath name = createString("name");

    public final ListPath<Participants, QParticipants> participants = this.<Participants, QParticipants>createList("participants", Participants.class, QParticipants.class, PathInits.DIRECT2);

    public final DatePath<java.time.LocalDate> startDate = createDate("startDate", java.time.LocalDate.class);

    public final ListPath<TravelDays, QTravelDays> travelDays = this.<TravelDays, QTravelDays>createList("travelDays", TravelDays.class, QTravelDays.class, PathInits.DIRECT2);

    public final NumberPath<Long> travelId = createNumber("travelId", Long.class);

    public final EnumPath<TravelStatus> travelStatus = createEnum("travelStatus", TravelStatus.class);

    public final EnumPath<TravelType> travelType = createEnum("travelType", TravelType.class);

    public final DateTimePath<java.time.LocalDateTime> updatedAt = createDateTime("updatedAt", java.time.LocalDateTime.class);

    public final NumberPath<Long> userId = createNumber("userId", Long.class);

    public QTravelPlans(String variable) {
        super(TravelPlans.class, forVariable(variable));
    }

    public QTravelPlans(Path<? extends TravelPlans> path) {
        super(path.getType(), path.getMetadata());
    }

    public QTravelPlans(PathMetadata metadata) {
        super(TravelPlans.class, metadata);
    }

}

