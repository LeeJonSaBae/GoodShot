package com.d201.goodshot.swing.domain;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QPose is a Querydsl query type for Pose
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QPose extends EntityPathBase<Pose> {

    private static final long serialVersionUID = -300874298L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QPose pose = new QPose("pose");

    public final NumberPath<Integer> code = createNumber("code", Integer.class);

    public final StringPath feedback = createString("feedback");

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final NumberPath<Double> similarity = createNumber("similarity", Double.class);

    public final QSwing swing;

    public final ArrayPath<byte[], Byte> swingImage = createArray("swingImage", byte[].class);

    public QPose(String variable) {
        this(Pose.class, forVariable(variable), INITS);
    }

    public QPose(Path<? extends Pose> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QPose(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QPose(PathMetadata metadata, PathInits inits) {
        this(Pose.class, metadata, inits);
    }

    public QPose(Class<? extends Pose> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.swing = inits.isInitialized("swing") ? new QSwing(forProperty("swing"), inits.get("swing")) : null;
    }

}

