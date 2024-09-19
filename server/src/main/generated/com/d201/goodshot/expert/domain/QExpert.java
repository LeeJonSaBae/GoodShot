package com.d201.goodshot.expert.domain;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;


/**
 * QExpert is a Querydsl query type for Expert
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QExpert extends EntityPathBase<Expert> {

    private static final long serialVersionUID = 1198551797L;

    public static final QExpert expert = new QExpert("expert");

    public final StringPath certificate = createString("certificate");

    public final NumberPath<Integer> expYears = createNumber("expYears", Integer.class);

    public final StringPath fields = createString("fields");

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final StringPath name = createString("name");

    public final StringPath topic = createString("topic");

    public QExpert(String variable) {
        super(Expert.class, forVariable(variable));
    }

    public QExpert(Path<? extends Expert> path) {
        super(path.getType(), path.getMetadata());
    }

    public QExpert(PathMetadata metadata) {
        super(Expert.class, metadata);
    }

}

