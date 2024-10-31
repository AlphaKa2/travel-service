package com.alphaka.travelservice.entity;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QTravelDays is a Querydsl query type for TravelDays
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QTravelDays extends EntityPathBase<TravelDays> {

    private static final long serialVersionUID = 793802402L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QTravelDays travelDays = new QTravelDays("travelDays");

    public final DatePath<java.time.LocalDate> date = createDate("date", java.time.LocalDate.class);

    public final NumberPath<Long> dayId = createNumber("dayId", Long.class);

    public final NumberPath<Integer> dayNumber = createNumber("dayNumber", Integer.class);

    public final QTravelPlans travelPlans;

    public final QTravelSchedules travelSchedules;

    public QTravelDays(String variable) {
        this(TravelDays.class, forVariable(variable), INITS);
    }

    public QTravelDays(Path<? extends TravelDays> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QTravelDays(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QTravelDays(PathMetadata metadata, PathInits inits) {
        this(TravelDays.class, metadata, inits);
    }

    public QTravelDays(Class<? extends TravelDays> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.travelPlans = inits.isInitialized("travelPlans") ? new QTravelPlans(forProperty("travelPlans")) : null;
        this.travelSchedules = inits.isInitialized("travelSchedules") ? new QTravelSchedules(forProperty("travelSchedules"), inits.get("travelSchedules")) : null;
    }

}

