package models.i18n;

import mongo.MongoEntity;

/**
 *
 * @author keruspe
 */
public abstract class TranslatableRef<T extends Translatable, R extends TranslatableRef> extends MongoEntity {
}
