package com.alphaka.travelservice.entity;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QTravelSchedules is a Querydsl query type for TravelSchedules
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QTravelSchedules extends EntityPathBase<TravelSchedules> {

    private static final long serialVersionUID = 1028223313L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QTravelSchedules travelSchedules = new QTravelSchedules("travelSchedules");

    public final DateTimePath<java.time.LocalDateTime> createdAt = createDateTime("createdAt", java.time.LocalDateTime.class);

    public final ListPath<TravelPlaces, QTravelPlaces> places = this.<TravelPlaces, QTravelPlaces>createList("places", TravelPlaces.class, QTravelPlaces.class, PathInits.DIRECT2);

    public final NumberPath<Long> scheduleId = createNumber("scheduleId", Long.class);

    public final QTravelDays travelDays;

    public final DateTimePath<java.time.LocalDateTime> updatedAt = createDateTime("updatedAt", java.time.LocalDateTime.class);

    public QTravelSchedules(String variable) {
        this(TravelSchedules.class, forVariable(variable), INITS);
    }

    public QTravelSchedules(Path<? extends TravelSchedules> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QTravelSchedules(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QTravelSchedules(PathMetadata metadata, PathInits inits) {
        this(TravelSchedules.class, metadata, inits);
    }

    public QTravelSchedules(Class<? extends TravelSchedules> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.travelDays = inits.isInitialized("travelDays") ? new QTravelDays(forProperty("travelDays"), inits.get("travelDays")) : null;
    }

}

