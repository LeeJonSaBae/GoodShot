package com.d201.goodshot.recommendation.domain;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;


/**
 * QNews is a Querydsl query type for News
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QNews extends EntityPathBase<News> {

    private static final long serialVersionUID = 1138893933L;

    public static final QNews news = new QNews("news");

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final StringPath newsImageUrl = createString("newsImageUrl");

    public final StringPath newsLink = createString("newsLink");

    public final StringPath subTitle = createString("subTitle");

    public final StringPath title = createString("title");

    public QNews(String variable) {
        super(News.class, forVariable(variable));
    }

    public QNews(Path<? extends News> path) {
        super(path.getType(), path.getMetadata());
    }

    public QNews(PathMetadata metadata) {
        super(News.class, metadata);
    }

}

