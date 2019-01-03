package com.example.android.appdeinventario;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by Andrik on 27/04/2018.
 */

public class InventarioDbHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "inventario.db";
    private static final int DATABASE_VERSION = 1;

    public InventarioDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String SQL_CREATE_INVENTARIO_TABLE = "CREATE TABLE " + ProdutoContract.ProdutoEntry.TABELA_NOME+ " ("
                +ProdutoContract.ProdutoEntry._ID+ " INTEGER PRIMARY KEY AUTOINCREMENT, "
                +ProdutoContract.ProdutoEntry.COLUNA_PRODUTO_NOME+ " TEXT NOT NULL, "
                +ProdutoContract.ProdutoEntry.COLUNA_PRODUTO_QUANTIDADE+ " INTEGER NOT NULL DEFAULT 0, "
                +ProdutoContract.ProdutoEntry.COLUNA_PRODUTO_PRECO+ " INTEGER NOT NULL, "
                +ProdutoContract.ProdutoEntry.COLUNA_PRODUTO_IMAGEM+ " BLOB NOT NULL, "
                +ProdutoContract.ProdutoEntry.COLUNA_PRODUTO_TELEFONE+ " INTEGER NOT NULL DEFAULT 0, "
                +ProdutoContract.ProdutoEntry.COLUNA_PRODUTO_EMAIL+ " TEXT); ";
        db.execSQL(SQL_CREATE_INVENTARIO_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
