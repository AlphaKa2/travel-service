package com.alphaka.travelservice.entity;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QTravelPlaces is a Querydsl query type for TravelPlaces
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QTravelPlaces extends EntityPathBase<TravelPlaces> {

    private static final long serialVersionUID = -1307088937L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QTravelPlaces travelPlaces = new QTravelPlaces("travelPlaces");

    public final StringPath address = createString("address");

    public final DateTimePath<java.time.LocalDateTime> createdAt = createDateTime("createdAt", java.time.LocalDateTime.class);

    public final NumberPath<java.math.BigDecimal> latitude = createNumber("latitude", java.math.BigDecimal.class);

    public final NumberPath<java.math.BigDecimal> longitude = createNumber("longitude", java.math.BigDecimal.class);

    public final NumberPath<Long> placeId = createNumber("placeId", Long.class);

    public final StringPath placeName = createString("placeName");

    public final QTravelSchedules travelSchedule;

    public QTravelPlaces(String variable) {
        this(TravelPlaces.class, forVariable(variable), INITS);
    }

    public QTravelPlaces(Path<? extends TravelPlaces> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QTravelPlaces(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QTravelPlaces(PathMetadata metadata, PathInits inits) {
        this(TravelPlaces.class, metadata, inits);
    }

    public QTravelPlaces(Class<? extends TravelPlaces> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.travelSchedule = inits.isInitialized("travelSchedule") ? new QTravelSchedules(forProperty("travelSchedule"), inits.get("travelSchedule")) : null;
    }

}

