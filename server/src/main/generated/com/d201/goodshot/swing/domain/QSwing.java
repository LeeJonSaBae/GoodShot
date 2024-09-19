package com.d201.goodshot.swing.domain;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QSwing is a Querydsl query type for Swing
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QSwing extends EntityPathBase<Swing> {

    private static final long serialVersionUID = -734168983L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QSwing swing = new QSwing("swing");

    public final NumberPath<Double> backSwingTime = createNumber("backSwingTime", Double.class);

    public final NumberPath<Double> downSwingTime = createNumber("downSwingTime", Double.class);

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final StringPath jointPoint = createString("jointPoint");

    public final ArrayPath<byte[], Byte> swingVideo = createArray("swingVideo", byte[].class);

    public final com.d201.goodshot.user.domain.QUser user;

    public QSwing(String variable) {
        this(Swing.class, forVariable(variable), INITS);
    }

    public QSwing(Path<? extends Swing> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QSwing(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QSwing(PathMetadata metadata, PathInits inits) {
        this(Swing.class, metadata, inits);
    }

    public QSwing(Class<? extends Swing> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.user = inits.isInitialized("user") ? new com.d201.goodshot.user.domain.QUser(forProperty("user")) : null;
    }

}

