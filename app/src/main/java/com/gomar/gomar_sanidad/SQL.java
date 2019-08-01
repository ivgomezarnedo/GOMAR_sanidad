package com.gomar.gomar_sanidad;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import org.acra.ReportField;
import org.acra.ReportingInteractionMode;
import org.acra.annotation.ReportsCrashes;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * In this class is defined all the functionality to create and allow interaction with the DB.
 */

//Para que si la aplicación falla se mande un correo electrónico a esa dirección con la información del error.
@ReportsCrashes(mailTo = "ivangomezarnedo@gmail.com", customReportContent = {
        ReportField.APP_VERSION_CODE, ReportField.APP_VERSION_NAME,
        ReportField.ANDROID_VERSION, ReportField.PHONE_MODEL,
        ReportField.CUSTOM_DATA, ReportField.STACK_TRACE, ReportField.LOGCAT},
        mode = ReportingInteractionMode.TOAST)
public class SQL extends SQLiteOpenHelper {

    /**
     * First it will be initialized all the fields and names of the different tables in order to not to have to change all of them (through all the Classes of this project)
     * if we have to redefine or change any of them. This functionality is introduced in order to respect the Separation into layers.
     */
    public static final String FichaTabla="ficha";
    public static final String ComponenteTabla="componente";
    public static final String FechaElaboracionFichaCampo="fecha_elaboracion";
    public static final String FechaPreferenteFichaCampo="fecha_preferente";
    public static final String KgFichaCampo="Kg_Producto";
    public static final String NombreFichaCampo="nombre_ficha";
    public static final String LoteFichaCampo="lote_ficha";
    public static final String PatronFichaCampo="esPatron";
    public static final String NombreComponenteCampo="nombre_componente";
    public static final String LoteComponenteCampo="lote_componente";
    public static final String ProveedorComponenteCampo="proveedor";
    public static final String KGComponenteCampo="Kg_Componente";
    public static final String FichaComponenteCampo="ficha";

    /**
     * Strings that will contain the Create Table queries to execute to create the tables.
     */
    private static final String Ficha_create = "CREATE TABLE IF NOT EXISTS "+FichaTabla+" (" +
            NombreFichaCampo+" TEXT , " +
            LoteFichaCampo+" String PRIMARY KEY," +
            FechaElaboracionFichaCampo+" DATE," +
            KgFichaCampo+" Double," +
            FechaPreferenteFichaCampo+" DATE," +
            PatronFichaCampo+" BOOLEAN)";
    private static final String Componente_create = "CREATE TABLE IF NOT EXISTS "+ComponenteTabla+" (" +
            NombreComponenteCampo+" TEXT , " +
            LoteComponenteCampo+" TEXT," +
            ProveedorComponenteCampo+" TEXT," +
            KGComponenteCampo+" Double," +
            FichaComponenteCampo+"  TEXT ," +
            "PRIMARY KEY("+LoteComponenteCampo+", "+FichaComponenteCampo+"),"+
            "FOREIGN KEY("+FichaComponenteCampo+") REFERENCES "+FichaTabla+"("+LoteFichaCampo+") ON DELETE CASCADE)";
    private static final String DB_NAME = "gomar";
    private static final int DB_VERSION = 1;
    private SQLiteDatabase db;

    /**
     * Instantiates a new Sql object. This object will allow communication with DB.
     *
     * @param context The application context.
     */
    public SQL(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
        db=this.getWritableDatabase();
        onCreate(db);
    }

    /**
     * This method will be called in the Creation of the SQL object. It will execute the commands to create the Tables of the DB (in case that these tables didn't exist)
     * It method will also change the property to allow the use of Constraints
     * @param db is the object that represent the SQLLITE Database.
     */
    @Override
    public void onCreate(SQLiteDatabase db) {

        //db.execSQL("DROP TABLE componente");
        //db.execSQL("DROP TABLE ficha");
        db.execSQL(Ficha_create);
        db.execSQL(Componente_create);
    }

    @Override
    public void onConfigure (SQLiteDatabase db){
        db.setForeignKeyConstraintsEnabled(true);
    }
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

    /**
     * Insert a new Ficha in the DB.
     *
     * @param cv The object that will contain the values of the row to insert (is like a map)
     */
    public void insertarFicha(ContentValues cv ){
        db.insert(FichaTabla, null, cv);
    }


    /**
     * Insert a new Componente in the DB.
     *
     * @param cv The object that will contain the values of the row to insert (is like a map)
     */
    public void insertarComponente(ContentValues cv ){
        db.insert(ComponenteTabla, null, cv);
    }

    /**
     * Insert a list of Componentes in the DB. Each element of the list will be a new row to insert in Componente table.
     *
     * @param componentes The list object that will containt all the Componente objects to insert
     */
    public void insertarComponentes(ArrayList<ContentValues> componentes ){
        for(ContentValues componente : componentes)
        {
            insertarComponente(componente);
        }
    }


    /**
     * This method will query the DB to get the different names of the different Fichas that are stored as pattern in DB.
     * @return An ArrayList object where each element will be a different Ficha name (String)
     */
    public ArrayList<String> getFichasPatron(){
        //Creamos el cursor
        ArrayList<String> lista=new ArrayList<String>();
        Cursor c = db.rawQuery("select distinct "+NombreFichaCampo+" from "+FichaTabla+" WHERE "+PatronFichaCampo+" ORDER BY "+NombreFichaCampo, null);
        if (c != null && c.getCount()>0) {
            c.moveToFirst();
            do {
                String nombre = c.getString(c.getColumnIndex(NombreFichaCampo));
                lista.add(nombre);
            } while (c.moveToNext());
        }
        //Cerramos el cursor
        c.close();
        return lista;
    }


    /**
     * This method will only serve to map the real values of the Table fields to a more pretty representation of those.
     * This method will be called from the differents interfaces of the application, in order to map the field that it shown to the user with the real value of this field.
     *
     * @return the hash map that contains this mapping.
     */
    public HashMap<String,String> getCampos(){

        //TODO No Hardcodear los campos de la BD. Inicializar al comienzo de la clase todos.
        HashMap<String,String> campos_BD_bonitos = new HashMap<String, String>();
        campos_BD_bonitos.put(ConsultarFichas.NombreFichaCampo,NombreFichaCampo);
        campos_BD_bonitos.put(ConsultarFichas.LoteFichaCampo,LoteFichaCampo);
        campos_BD_bonitos.put(ConsultarFichas.FechaElaboracionFichaCampo,FechaElaboracionFichaCampo);
        campos_BD_bonitos.put(ConsultarFichas.KgFichaCampo,KgFichaCampo);
        campos_BD_bonitos.put(ConsultarFichas.FechaPreferenteFichaCampo,FechaPreferenteFichaCampo);
        campos_BD_bonitos.put(ConsultarFichas.NombreComponenteCampo,NombreComponenteCampo);
        campos_BD_bonitos.put(ConsultarFichas.LoteComponenteCampo,LoteComponenteCampo);
        campos_BD_bonitos.put(ConsultarFichas.ProveedorComponenteCampo,ProveedorComponenteCampo);
        campos_BD_bonitos.put(ConsultarFichas.KGComponenteCampo,KGComponenteCampo);
        return campos_BD_bonitos;
    }


    /**
     * This method will query the DB to get a certain Ficha, performing a query by it's Ficha's name.
     *
     * @param nombreficha the Name of the Ficha that will be searched.
     * @return a Ficha object with all the values of that Ficha searched.
     */
//Obtener la lista de Fichas en la base de datos
    public Ficha getFichaByNombre(String nombreficha){
        //Creamos el cursor
        Cursor c = db.rawQuery("select * from "+FichaTabla+" WHERE "+NombreFichaCampo+"=?", new String[]{nombreficha});
        if (c != null && c.getCount()>0) {
            c.moveToFirst();
            String nombre = c.getString(c.getColumnIndex(NombreFichaCampo));
            String lote = c.getString(c.getColumnIndex(LoteFichaCampo));
            String fecha_elaboracion = c.getString(c.getColumnIndex(FechaElaboracionFichaCampo));
            Double Kg_Producto = c.getDouble(c.getColumnIndex(KgFichaCampo));
            String fecha_preferente = c.getString(c.getColumnIndex(FechaPreferenteFichaCampo));
            return (new Ficha(nombre,lote,fecha_elaboracion,Kg_Producto,fecha_preferente));
        }
        //Cerramos el cursor
        c.close();
        return null;
    }

    /**
     * This method will query the DB to get a certain Ficha, performing a query by it's Ficha's Lote.
     *
     * @param lote The Lote (ID) of the Ficha that will be aearched.
     * @return a Ficha object with all the values of that Ficha searched.
     */
    public Ficha getFichaByLote(String lote){
        //Creamos el cursor
        Cursor c = db.rawQuery("select * from "+FichaTabla+" WHERE "+LoteFichaCampo+"=?", new String[]{lote});
        if (c != null && c.getCount()>0) {
            c.moveToFirst();
            String nombre = c.getString(c.getColumnIndex(NombreFichaCampo));
            String lote_ficha = c.getString(c.getColumnIndex(LoteFichaCampo));
            String fecha_elaboracion = c.getString(c.getColumnIndex(FechaElaboracionFichaCampo));
            Double Kg_Producto = c.getDouble(c.getColumnIndex(KgFichaCampo));
            String fecha_preferente = c.getString(c.getColumnIndex(FechaPreferenteFichaCampo));
            return (new Ficha(nombre,lote_ficha,fecha_elaboracion,Kg_Producto,fecha_preferente));
        }
        //Cerramos el cursor
        c.close();
        return null;
    }

    /**
     * Remove a Ficha by it´s Lote's value.
     *
     * @param lote The Lote(ID) of the Ficha to remove.
     */
    public void removeFichaByLote(String lote){
        System.out.println(db.delete(FichaTabla,LoteFichaCampo+"=?",new String[]{lote}));    }


    /**
     * Return an ArrayList with all the Componentes of a Ficha
     *
     * @param lote_ficha The Lote (ID) of the Ficha to search
     * @return An ArrayList where each element will be a Componente object
     */
    public  ArrayList<Componente> getComponentesByFicha(String lote_ficha){
        //Creamos el cursor
        ArrayList<Componente> lista=new ArrayList<Componente>();
        Cursor c = db.rawQuery("select * from "+ComponenteTabla+" WHERE "+FichaComponenteCampo+"=?",new String[]{lote_ficha});
        if (c != null && c.getCount()>0) {
            c.moveToFirst();
            do {
                String nombre = c.getString(c.getColumnIndex(NombreComponenteCampo));
                String lote_componente = c.getString(c.getColumnIndex(LoteComponenteCampo));
                String proveedor = c.getString(c.getColumnIndex(ProveedorComponenteCampo));
                Double Kg_Componente = c.getDouble(c.getColumnIndex(KGComponenteCampo));
                lista.add(new Componente(nombre,lote_componente,proveedor,Kg_Componente,lote_ficha));
            } while (c.moveToNext());
        }
        //Cerramos el cursor
        c.close();
        return lista;
    }


    /**
     * A method that will check if a certain @lote_ficha is in the DB
     *
     * @param lote_ficha The Lote(ID) to check
     * @return TRUE if it's present in the DB and FALSE if not
     */
    public boolean loteRepetido (String lote_ficha)
    {
        Cursor c = db.rawQuery("select "+LoteFichaCampo+" from "+FichaTabla+" where "+LoteFichaCampo+"=?",new String[]{lote_ficha});
        if (c != null && c.getCount()>0) {
            c.close();
            return true;
        }
        c.close();
        return false;
    }

    /**
     * This method will create the result query of all the Conditions (Condicion) typed by the user in the activity 'ConsultarFichas'.
     * Then, this result query will be executed and all the LoteFichaCampo results will be stored in an ArrayList that will be returned
     *
     * @param condiciones The ArrayList object that will contain all the Condicion objects
     * @return An ArrayList with all the Ficha's lotes results of execute the previously generated query.
     */
    public ArrayList<String> constructorDeCondiciones(ArrayList<Condicion> condiciones )
    {
        ArrayList<String> lotesficha=new ArrayList<>();
        String query="SELECT DISTINCT "+LoteFichaCampo+" " +
                " FROM "+FichaTabla+" as f join "+ComponenteTabla+" as c on f."+LoteFichaCampo+"=c."+FichaComponenteCampo+" " +
                " WHERE ";
        for(Condicion condicion : condiciones)
        {
            //Añadimos AND siempre que la condición no sea la última.
            if(condiciones.indexOf(condicion)==condiciones.size()-1)
            {
                query=query+condicion.toString();
            }
            else
            {
                query=query+condicion.toString()+" AND ";
            }
        }
        //Ahora ejecutamos la query anterior.
        Cursor c = db.rawQuery(query, null);
        if (c != null && c.getCount()>0) {
            c.moveToFirst();
            do {
                lotesficha.add(c.getString(c.getColumnIndex(LoteFichaCampo)));

            } while (c.moveToNext());
        }
        c.close();
        return lotesficha;
    }

    /**
     * This method will be the core functionality to add predictive text.
     * It will be called from the Activities in which the user has to type something and it will search the results of a certain text (typed by the user)
     * in a certain field (Campo) of a certain table (Tabla). The text will be transformed to be case insensitive and it will be used regular expressions to search not only
     * by the start of a word. The results will be ordered by the similarity of their starting letters with the text typed by the user.
     * @param campo The field of the @tabla to be searched in
     * @param tabla The table to search in
     * @param texto The text (typed by the user) to look for
     * @return An ArrayList with all the results of this @campo
     */
    public ArrayList<String> busquedaLike(String campo,String tabla, String texto)
    {
        String query= "SELECT DISTINCT "+campo+
                " FROM "+FichaTabla+" as f JOIN "+ComponenteTabla+" as c  on f."+LoteFichaCampo+"=c."+FichaComponenteCampo+
                " WHERE upper("+campo+") like '%"+texto.toUpperCase()+"%'"+
                " order by" +
                " case when upper("+campo+") like '"+texto.toUpperCase()+"%' then 0 else 1 end, " +
                campo;
        ArrayList<String> lista=new ArrayList<String>();
        System.out.println(query);
        Cursor c = db.rawQuery(query, null);
        if (c != null && c.getCount()>0) {
            c.moveToFirst();
            do {
                String nombre = c.getString(c.getColumnIndex(campo));
                lista.add(nombre);
            } while (c.moveToNext());
        }
        //Cerramos el cursor
        c.close();
        return lista;
    }
}

