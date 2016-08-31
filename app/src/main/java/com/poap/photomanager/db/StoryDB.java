package com.poap.photomanager.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class StoryDB extends SQLiteOpenHelper {
    public static final int DATABASE_VERSION = 1;
    public static final String DATABASE_NAME = "Story.db";

    public StoryDB(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE story (_id INTEGER PRIMARY KEY, title TEXT, memo TEXT, " +
                "edited TIMESTAMP DEFAULT CURRENT_TIMESTAMP)");
//        db.execSQL("INSERT INTO story (title, memo, edited) VALUES ('from google image3', 'memo4', '2016-06-10 13:55:55')");
//        db.execSQL("INSERT INTO story (title, memo, edited) VALUES ('from google image4', 'memo5', '2016-06-10 13:55:55')");
//        db.execSQL("INSERT INTO story (title, memo, edited) VALUES ('from google image5', 'memo6', '2016-07-10 13:55:55')");
//        db.execSQL("INSERT INTO story (title, memo, edited) VALUES ('from google image6', 'memo1', '2016-08-10 13:55:55')");
//        db.execSQL("INSERT INTO story (title, memo, edited) VALUES ('from google image1', 'memo2', '2015-09-10 13:55:55')");
//        db.execSQL("INSERT INTO story (title, memo) VALUES ('from google image2', 'memo3')");
        db.execSQL("CREATE TABLE picture (_id INTEGER PRIMARY KEY, path TEXT, story INTEGER NOT NULL, FOREIGN KEY(story) REFERENCES story(_id))");
//        db.execSQL("INSERT INTO picture (path, story) VALUES ('https://pixabay.com/static/uploads/photo/2015/10/01/21/39/background-image-967820_960_720.jpg', '1')");
//        db.execSQL("INSERT INTO picture (path, story) VALUES ('http://imguol.com/c/noticias/2013/12/13/13dez2013---esta-imagem-mostra-a-nebulosa-de-caranguejo-um-iconico-remanescente-de-supernova-na-nossa-galaxia-vista-do-observatorio-espacial-herschel-e-do-telescopio-hubble-uma-nuvem-de-gas-e-poeira-1386961235961_956x500.jpg', '2')");
//        db.execSQL("INSERT INTO picture (path, story) VALUES ('http://www.photonics.com/images/Web/Articles/2012/2/13/thumbnail_50102.jpg', '3')");
//        db.execSQL("INSERT INTO picture (path, story) VALUES ('http://i.telegraph.co.uk/multimedia/archive/03589/Wellcome_Image_Awa_3589699k.jpg', '4')");
//        db.execSQL("INSERT INTO picture (path, story) VALUES ('http://www.qqxxzx.com/images/image/image-16.png', '5')");
//        db.execSQL("INSERT INTO picture (path, story) VALUES ('http://www.spyderonlines.com/images/wallpapers/image/image-11.jpg', '6')");
        System.out.println("New tables edited");
        System.out.println("New tables edited");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        //TODO: implement migration stuff
    }

}
