package com.example.android.appdeinventario;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.util.Log;
import android.widget.Toast;

/**
 * Created by Andrik on 28/04/2018.
 */

public class InventarioProvider extends ContentProvider {

    private static final int PRODUTOS = 100;
    private static final int PRODUTOS_ID = 101;
    private static final UriMatcher sUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);

    static {
        sUriMatcher.addURI(ProdutoContract.CONTENT_AUTHORITY,ProdutoContract.PATH_PRODUTOS,PRODUTOS);
        sUriMatcher.addURI(ProdutoContract.CONTENT_AUTHORITY,ProdutoContract.PATH_PRODUTOS+"/#",PRODUTOS_ID);
    }

    private InventarioDbHelper mDbHelper;

    @Override
    public boolean onCreate() {
        mDbHelper = new InventarioDbHelper(getContext());
        return true;
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        SQLiteDatabase database = mDbHelper.getReadableDatabase();
        Cursor cursor = null;
        int match = sUriMatcher.match(uri);
        switch (match) {
            case PRODUTOS:
                cursor = database.query(ProdutoContract.ProdutoEntry.TABELA_NOME, projection, selection, selectionArgs, null, null, sortOrder);
                break;
            case PRODUTOS_ID:
                selection = ProdutoContract.ProdutoEntry._ID + "=?";
                selectionArgs = new String[] { String.valueOf(ContentUris.parseId(uri)) };
                cursor = database.query(ProdutoContract.ProdutoEntry.TABELA_NOME, projection, selection, selectionArgs, null, null, sortOrder);
                break;
            default:
                throw new IllegalArgumentException("Falha em query:"+uri);
        }
        cursor.setNotificationUri(getContext().getContentResolver(),uri);
        return cursor;
    }

    @Override
    public String getType(Uri uri) {
        final int match = sUriMatcher.match(uri);
        switch (match) {
            case PRODUTOS:
                return ProdutoContract.ProdutoEntry.CONTENT_LIST_TYPE;
            case PRODUTOS_ID:
                return ProdutoContract.ProdutoEntry.CONTENT_ITEM_TYPE;
            default:
                throw new IllegalStateException("Falha em getType: uri> " + uri + " match> " + match);
        }
    }

    @Override
    public Uri insert( Uri uri, ContentValues values) {
        Uri newUri=null;
        try {
            validarInsert(values);
            SQLiteDatabase database = mDbHelper.getWritableDatabase();
            long id = database.insert(ProdutoContract.ProdutoEntry.TABELA_NOME,null,values);
            if(id == -1) {
                Log.e(InventarioProvider.class.getSimpleName(),"Falha em insert:"+uri);
            }
            else
                newUri = ContentUris.withAppendedId(uri, id);
        }
        catch (IllegalArgumentException e){
            Toast.makeText(getContext(),"Erro: "+e.getMessage(),Toast.LENGTH_LONG).show();
        }
        getContext().getContentResolver().notifyChange(uri,null);
        return newUri;
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        SQLiteDatabase database = mDbHelper.getWritableDatabase();
        int rowsDeleted = 0;
        final int match = sUriMatcher.match(uri);
        switch (match) {
            case PRODUTOS:
                rowsDeleted = database.delete(ProdutoContract.ProdutoEntry.TABELA_NOME, selection, selectionArgs);
                break;
            case PRODUTOS_ID:
                selection = ProdutoContract.ProdutoEntry._ID + "=?";
                selectionArgs = new String[] { String.valueOf(ContentUris.parseId(uri)) };
                rowsDeleted =database.delete(ProdutoContract.ProdutoEntry.TABELA_NOME, selection, selectionArgs);
                break;
        }

        if (rowsDeleted != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }
        return rowsDeleted;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        if(values.size()==0)
            return 0;
        int rowsUpdated = 0;
        try {
            validarUpdate(values);
            SQLiteDatabase database = mDbHelper.getWritableDatabase();
            final int match = sUriMatcher.match(uri);
            switch (match) {
                case PRODUTOS:
                    rowsUpdated = database.update(ProdutoContract.ProdutoEntry.TABELA_NOME, values, selection, selectionArgs);
                    break;
                case PRODUTOS_ID:
                    selection = ProdutoContract.ProdutoEntry._ID + "=?";
                    selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};
                    rowsUpdated = database.update(ProdutoContract.ProdutoEntry.TABELA_NOME, values, selection, selectionArgs);
                    break;
                default:
                    throw new IllegalArgumentException("Falha em update: " + uri);
            }
        }
        catch (IllegalArgumentException e){
            Toast.makeText(getContext(),"Erro: "+e.getMessage(),Toast.LENGTH_LONG).show();
        }
        if (rowsUpdated != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }
        return rowsUpdated;
    }

    private void validarInsert(ContentValues values){
        String nome = values.getAsString(ProdutoContract.ProdutoEntry.COLUNA_PRODUTO_NOME);
        if(nome == null || nome.equals(""))
            throw new IllegalArgumentException("Nome do produto esta invalido.");
        Integer quantidade = values.getAsInteger(ProdutoContract.ProdutoEntry.COLUNA_PRODUTO_QUANTIDADE);
        if(quantidade == null || quantidade.equals("") || quantidade < 0)
            throw new IllegalArgumentException("Quantidade do produto esta invalido.");
        Integer preco = values.getAsInteger(ProdutoContract.ProdutoEntry.COLUNA_PRODUTO_PRECO);
        if(preco == null || preco.equals("") || preco < 0)
            throw new IllegalArgumentException("Preço do produto esta invalido.");
        byte [] imagem = values.getAsByteArray(ProdutoContract.ProdutoEntry.COLUNA_PRODUTO_IMAGEM);
        if(imagem == null)
            throw new IllegalArgumentException("Imagem do produto esta invalido.");
        Integer telefone = values.getAsInteger(ProdutoContract.ProdutoEntry.COLUNA_PRODUTO_TELEFONE);
        String email = values.getAsString(ProdutoContract.ProdutoEntry.COLUNA_PRODUTO_EMAIL);
        if(telefone == null || telefone.equals("") || telefone<0)//aceita pelo menos um item correto: ou telefone ou email
            if(email == null || email.equals(""))
                throw new IllegalArgumentException("Telefone ou email do produto esta invalido.");
    }

    private void validarUpdate(ContentValues values){
        if(values.containsKey(ProdutoContract.ProdutoEntry.COLUNA_PRODUTO_NOME)) {
            String nome = values.getAsString(ProdutoContract.ProdutoEntry.COLUNA_PRODUTO_NOME);
            if (nome == null || nome.equals(""))
                throw new IllegalArgumentException("Nome do produto esta invalido.");
        }
        if(values.containsKey(ProdutoContract.ProdutoEntry.COLUNA_PRODUTO_QUANTIDADE)) {
            Integer quantidade = values.getAsInteger(ProdutoContract.ProdutoEntry.COLUNA_PRODUTO_QUANTIDADE);
            if (quantidade == null || quantidade.equals("") || quantidade < 0)
                throw new IllegalArgumentException("Quantidade do produto esta invalido.");
        }
        if(values.containsKey(ProdutoContract.ProdutoEntry.COLUNA_PRODUTO_PRECO)) {
            Integer preco = values.getAsInteger(ProdutoContract.ProdutoEntry.COLUNA_PRODUTO_PRECO);
            if (preco == null || preco.equals("") || preco < 0)
                throw new IllegalArgumentException("Preço do produto esta invalido.");
        }
        if(values.containsKey(ProdutoContract.ProdutoEntry.COLUNA_PRODUTO_IMAGEM)) {
            byte [] imagem = values.getAsByteArray(ProdutoContract.ProdutoEntry.COLUNA_PRODUTO_IMAGEM);
            if(imagem == null)
            throw new IllegalArgumentException("Imagem do produto esta invalido.");
        }
        if(values.containsKey(ProdutoContract.ProdutoEntry.COLUNA_PRODUTO_TELEFONE)) {
            Integer telefone = values.getAsInteger(ProdutoContract.ProdutoEntry.COLUNA_PRODUTO_TELEFONE);
            if(telefone == null || telefone.equals("") || telefone<0)
                throw new IllegalArgumentException("Telefone do produto esta invalido.");
        }
        if(values.containsKey(ProdutoContract.ProdutoEntry.COLUNA_PRODUTO_EMAIL)) {
            String email = values.getAsString(ProdutoContract.ProdutoEntry.COLUNA_PRODUTO_EMAIL);
            if (email == null || email.equals(""))
                throw new IllegalArgumentException("Telefone ou email do produto esta invalido.");
        }
    }

}
