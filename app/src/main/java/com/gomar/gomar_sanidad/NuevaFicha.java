package com.gomar.gomar_sanidad;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.TextView;
import android.widget.Toast;
import org.acra.ReportField;
import org.acra.ReportingInteractionMode;
import org.acra.annotation.ReportsCrashes;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

//TODO INICIALIZAR TODOS LOS TEXTVIEW EN EL MÉTODO ONCREATE (hacerlos  globales)

/**
 * The type Nueva ficha.
 */
public class NuevaFicha extends AppCompatActivity {
    /**
     * The Fruits.
     */
    String[] fruits = {"Apple", "Banana", "Cherry", "Date", "Grape", "Kiwi", "Mango", "Pear"};
    /**
     * The Fecha consumo.
     */
    EditText fechaConsumo = null;
    /**
     * The Fecha elaboracion.
     */
    EditText fechaElaboracion = null;
    /**
     * The Calendar elaboracion.
     */
    final Calendar calendarElaboracion = Calendar.getInstance();
    /**
     * The Calendar consumo.
     */
    final Calendar calendarConsumo = Calendar.getInstance();
    private Button btn_crear;
    private Button btn_eliminar;
    /**
     * The Componentes.
     */
    int componentes = 0;
    /**
     * The Sgbd.
     */
    SQL sgbd = null;
    /**
     * The Elementoclickado.
     */
    int elementoclickado=1;
    /**
     * The Actual.
     */
    Context actual=this;





    private View.OnClickListener btn_crearListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            ClickCrear();
        }
    };
    private View.OnClickListener btn_eliminarListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            ClickEliminar();
        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        sgbd = new SQL(this);
        setContentView(R.layout.activity_nueva_ficha);
        Toolbar myToolbar = (Toolbar) findViewById(R.id.my_toolbar);
        setSupportActionBar(myToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        //Creating the instance of ArrayAdapter containing list of fruit names
        ArrayAdapter<String> adapter = new ArrayAdapter<String>
                (this, android.R.layout.select_dialog_item, fruits);
        //Getting the instance of AutoCompleteTextView
        AutoCompleteTextView actv = (AutoCompleteTextView) findViewById(R.id.TVNombrePrep);
        actv.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                ArrayList<String> listanombres=sgbd.busquedaLike("nombre_ficha","",s.toString());
                ArrayAdapter<String> adapter = new ArrayAdapter<String>
                        (actual, android.R.layout.select_dialog_item, listanombres.toArray(new String[0]));
                //Getting the instance of AutoCompleteTextView
                AutoCompleteTextView actv = (AutoCompleteTextView) findViewById(R.id.TVNombrePrep);
                actv.setThreshold(1);//will start working from first character
                actv.setAdapter(adapter);//setting the adapter data into the AutoCompleteTextView
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        //actv.setTextColor(Color.RED);
        fechaConsumo = (EditText) findViewById(R.id.TVFechaConsum);
        fechaElaboracion = (EditText) findViewById(R.id.SeleccionarCampos);
        updateLabelElaboracion();
        updateLabelPreferente();
        btn_crear = (Button) findViewById(R.id.btn_nuevo_comp);
        btn_crear.setOnClickListener(btn_crearListener);
        btn_eliminar= (Button) findViewById(R.id.btn_elimin_comp);
        btn_eliminar.setOnClickListener(btn_eliminarListener);



        final DatePickerDialog.OnDateSetListener dateElaboracion = new DatePickerDialog.OnDateSetListener() {

            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear,
                                  int dayOfMonth) {
                // TODO Auto-generated method stub
                calendarElaboracion.set(Calendar.YEAR, year);
                calendarElaboracion.set(Calendar.MONTH, monthOfYear);
                calendarElaboracion.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                updateLabelPreferente();
            }

        };

        final DatePickerDialog.OnDateSetListener dateConsumo = new DatePickerDialog.OnDateSetListener() {

            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear,
                                  int dayOfMonth) {
                // TODO Auto-generated method stub
                calendarConsumo.set(Calendar.YEAR, year);
                calendarConsumo.set(Calendar.MONTH, monthOfYear);
                calendarConsumo.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                updateLabelElaboracion();
            }

        };

        fechaConsumo.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                new DatePickerDialog(NuevaFicha.this, dateConsumo, calendarConsumo
                        .get(Calendar.YEAR), calendarConsumo.get(Calendar.MONTH),
                        calendarConsumo.get(Calendar.DAY_OF_MONTH)).show();
                updateLabelElaboracion();
            }
        });

        fechaElaboracion.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                new DatePickerDialog(NuevaFicha.this, dateElaboracion, calendarElaboracion
                        .get(Calendar.YEAR), calendarElaboracion.get(Calendar.MONTH),
                        calendarElaboracion.get(Calendar.DAY_OF_MONTH)).show();
                updateLabelPreferente();

            }
        });


    }

    private void updateLabelElaboracion() {
        String myFormat = "dd/MM/yy"; //In which you need put here
        SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);
        fechaConsumo.setText(sdf.format(calendarConsumo.getTime()));

    }
    private void updateLabelPreferente() {
        /*
        Aquí cogeremos la primera fecha de consumo preferente que se introdujo para la creación de la ficha patrón, calcularemos los días desde el día actual
        hasta dicha fecha y siempre que generemos una ficha similar le sumaremos esos días con respecto al día de creación del elaborado.
         */
        String myFormat = "dd/MM/yy"; //In which you need put here
        SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);
        fechaElaboracion.setText(sdf.format(calendarElaboracion.getTime()));

    }

    private void ClickCrear() {
        final LinearLayout layout_comp = (LinearLayout) findViewById(R.id.layout_componentes);
        layout_comp.setOrientation(LinearLayout.VERTICAL);
        LayoutParams layoutparams = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);


        LayoutParams LayoutParamsview = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);

        //Creating textview .
        componentes++;
        TextView textViewComp=new TextView(this);
        textViewComp.setText("Componente "+componentes);
        textViewComp.setTag("Texto"+componentes);
        AutoCompleteTextView auto_nombre = new AutoCompleteTextView(this);
        auto_nombre.setHint("Introduce el nombre del componente "+componentes);
        auto_nombre.setTag("Nombre"+componentes);
        auto_nombre.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                ArrayList<String> listanombres=sgbd.busquedaLike("nombre_componente","",s.toString());
                ArrayAdapter<String> adapter = new ArrayAdapter<String>
                        (actual, android.R.layout.select_dialog_item, listanombres.toArray(new String[0]));
                //Getting the instance of AutoCompleteTextView
                AutoCompleteTextView actv = (AutoCompleteTextView) layout_comp.findViewWithTag("Nombre"+componentes);
                actv.setThreshold(1);//will start working from first character
                actv.setAdapter(adapter);//setting the adapter data into the AutoCompleteTextView
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        AutoCompleteTextView auto_proveedor = new AutoCompleteTextView(this);
        auto_proveedor.setTag("Proveedor"+componentes);
        auto_proveedor.setHint("Introduce el nombre del proveedor "+componentes);
        auto_proveedor.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                ArrayList<String> listanombres=sgbd.busquedaLike("proveedor","",s.toString());
                ArrayAdapter<String> adapter = new ArrayAdapter<String>
                        (actual, android.R.layout.select_dialog_item, listanombres.toArray(new String[0]));
                //Getting the instance of AutoCompleteTextView
                AutoCompleteTextView actv = (AutoCompleteTextView) layout_comp.findViewWithTag("Proveedor"+componentes);
                actv.setThreshold(1);//will start working from first character
                actv.setAdapter(adapter);//setting the adapter data into the AutoCompleteTextView
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        AutoCompleteTextView auto_lote = new AutoCompleteTextView(this);
        auto_lote.setTag("Lote"+componentes);
        auto_lote.setHint("Introduce el lote del componente "+componentes);
        AutoCompleteTextView auto_peso = new AutoCompleteTextView(this);
        auto_peso.setTag("Peso"+componentes);
        auto_peso.setHint("Introduce el peso del componente "+componentes);

        textViewComp.setLayoutParams(LayoutParamsview);
        auto_nombre.setLayoutParams(LayoutParamsview);
        auto_proveedor.setLayoutParams(LayoutParamsview);
        auto_lote.setLayoutParams(LayoutParamsview);
        auto_peso.setLayoutParams(LayoutParamsview);

        //Adding textview to linear layout using Add View function.
        layout_comp.addView(textViewComp);
        layout_comp.addView(auto_nombre);
        layout_comp.addView(auto_proveedor);
        layout_comp.addView(auto_lote);
        layout_comp.addView(auto_peso);

    }

    private void ClickEliminar() {
        if(componentes!=0)
        {
            LinearLayout layout_comp = (LinearLayout) findViewById(R.id.layout_componentes);
            layout_comp.removeView(layout_comp.findViewWithTag(("Texto"+componentes)));
            layout_comp.removeView(layout_comp.findViewWithTag(("Nombre"+componentes)));
            layout_comp.removeView(layout_comp.findViewWithTag(("Proveedor"+componentes)));
            layout_comp.removeView(layout_comp.findViewWithTag(("Lote"+componentes)));
            layout_comp.removeView(layout_comp.findViewWithTag(("Peso"+componentes)));
            componentes--;
        }

    }
    @Override //Hace lo mismo que un listener para capturar el click en el botón de volver atrás.
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.example_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Boolean resultado=true;
        switch (item.getItemId()) {
            case R.id.guardar:
                 resultado=CompruebayGuarda(false);
                if(resultado)
                    Toast.makeText(this, "Ficha introducida correctamente en la BD", Toast.LENGTH_SHORT).show();
                return true;
            case R.id.item2:
                importarPatron();
                return true;
            case R.id.guardarpatron:
                resultado=CompruebayGuarda(true);
                if(resultado)
                    Toast.makeText(this, "Ficha introducida correctamente en la BD y como patrón", Toast.LENGTH_SHORT).show();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    /**
     * Insercion en bd.
     */
    public void InsercionEnBD() {
        if (sgbd != null) {
            //Aquí añadimos los valores de los diferentes componentes a la BD.
        }

    }

    /**
     * Compruebay guarda boolean.
     *
     * @param patron the patron
     * @return the boolean
     */
    public Boolean CompruebayGuarda(Boolean patron) {
        //Aqui comprobamos todos los campos en los que el usuario puede insertar texto, de forma que, si cumplen un determinado formato, los insertaremos en la BD.
        /*
        CLAVES PARA EL FORMATEO DE LOS DATOS:
        -Comprobar que no haya ningún campo vacío.
        -Comprobar que no haya valores de texto en campos donde no debería haber texto.
        -Comprobar que la fecha de elaboración no es posterior a la fecha de caducidad.
        -Comprobar que el lote del producto no está repetido (Quizá debería de generar yo el lote de los diferentes productos y no permitirle al usuario cambiarlo)
        -Para evitar tener varias fichas o componentes en la BD que sean iguales pero con distinto nombre, quizá sería conveniente mostrar por pantalla una lista de
        los diferentes componentes/Fichas (sus nombres) ordenados alfabéticamente si el usuario introduce un nombre que no se encuentre en la BD.

         */
        TextView TVNombre = (TextView) findViewById(R.id.TVNombrePrep);
        TextView TVLote = (TextView) findViewById(R.id.TVLote);
        TextView TVKg = (TextView) findViewById(R.id.TVKg);
        TextView TVFechaElab = (TextView) findViewById(R.id.SeleccionarCampos);
        TextView TVFechaConsum = (TextView) findViewById(R.id.TVFechaConsum);

        if (vacioOnulo(TVNombre.getText().toString())) {
            Toast.makeText(this, "Introduce un NOMBRE para el preparado", Toast.LENGTH_SHORT).show();
            return false;
        } else if (!ComprobarFormatoLote(TVLote.getText().toString())) {
            Toast.makeText(this, "Introduce un LOTE con el formato: XXXX012345 (4 letras y 5 números).", Toast.LENGTH_SHORT).show();
            return false;
        } else if (sgbd.getFichaByLote(TVLote.getText().toString()) != null) {
            Toast.makeText(this, "Introduce un LOTE para la Ficha que no se encuentre ya en la base de datos.", Toast.LENGTH_SHORT).show();
            return false;
        } else if (vacioOnulo(TVKg.getText().toString()) || Double.parseDouble(TVKg.getText().toString()) <= 0.0) {
            Toast.makeText(this, "Introduce los KG del preparado y comprueba que el número introducido es >=0", Toast.LENGTH_SHORT).show();
            return false;
        } else if (vacioOnulo(TVFechaElab.getText().toString())) {
            Toast.makeText(this, "Introduce la FECHA DE ELABORACIÓN  del preparado", Toast.LENGTH_SHORT).show();
            return false;
        } else if (vacioOnulo(TVFechaConsum.getText().toString()) && comparaFechas(TVFechaElab.getText().toString(), TVFechaConsum.getText().toString())) {
            Toast.makeText(this, "Introduce la FECHA DE CONSUMO PREFERENTE del preparado " +
                    "y comprueba que esta fecha es posterior (o igual) a la de ELABORACIÓN", Toast.LENGTH_SHORT).show();
            return false;
        } else if (componentes == 0) {
            Toast.makeText(this, "No has introducido ningún COMPONENTE", Toast.LENGTH_SHORT).show();
            return false;
        } else {
            ArrayList<ContentValues> componentes_values=new ArrayList<>();
            ContentValues cv = new ContentValues();
            cv.put("nombre_ficha", TVNombre.getText().toString());
            cv.put("lote_ficha", TVLote.getText().toString());
            cv.put("Kg_Producto", Double.parseDouble(TVKg.getText().toString()));
            cv.put("fecha_elaboracion", TVFechaElab.getText().toString());
            cv.put("fecha_preferente", TVFechaConsum.getText().toString());
            cv.put("esPatron", patron);
            LinearLayout layout_comp = (LinearLayout) findViewById(R.id.layout_componentes);
            TextView TVComNom = null;
            TextView TVComProv = null;
            TextView TVComLote = null;
            TextView TVComKg = null;
            Boolean comprobaciones=true;
            for (int i = 1; i <= componentes && comprobaciones; i++) {
                TVComNom = (TextView) layout_comp.findViewWithTag(("Nombre" + i));
                TVComProv = (TextView) layout_comp.findViewWithTag(("Proveedor" + i));
                TVComLote = (TextView) layout_comp.findViewWithTag(("Lote" + i));
                TVComKg = (TextView) layout_comp.findViewWithTag(("Peso" + i));
                if (vacioOnulo(TVComNom.getText().toString())) {
                    Toast.makeText(this, "Introduce un NOMBRE para el componente" + i, Toast.LENGTH_SHORT).show();
                    comprobaciones=false;
                } else if (vacioOnulo(TVComProv.getText().toString())) {
                    Toast.makeText(this, "Introduce un PROVEEDOR para el componente" + i, Toast.LENGTH_SHORT).show();
                    comprobaciones=false;
                } else if (vacioOnulo(TVComLote.getText().toString())) {
                    Toast.makeText(this, "Introduce un LOTE para el componente" + i, Toast.LENGTH_SHORT).show();
                    comprobaciones=false;

                }
                else if (sgbd.getFichaByLote(TVLote.getText().toString()) != null) {
                    Toast.makeText(this, "Introduce un LOTE para la Ficha que no se encuentre ya en la base de datos.", Toast.LENGTH_SHORT).show();
                    comprobaciones=false;
                }
                else if (vacioOnulo(TVComKg.getText().toString())) {
                    try {
                        if (Double.parseDouble(TVComKg.getText().toString()) <= 0.0) {
                            throw new NumberFormatException();
                        }
                    } catch (NumberFormatException ne) {
                        Toast.makeText(this, "Introduce los KG para el componente " + i + " y comprueba que el número introducido es >=0", Toast.LENGTH_SHORT).show();
                        comprobaciones=false;
                    }
                }
                else
                {
                    ContentValues cv2 = new ContentValues();
                    cv2.put("nombre_componente", TVComNom.getText().toString());
                    cv2.put("lote_componente", TVComLote.getText().toString());
                    cv2.put("proveedor", TVComProv.getText().toString());
                    cv2.put("Kg_Componente", Double.parseDouble(TVComKg.getText().toString()));
                    cv2.put("ficha", TVLote.getText().toString());
                    componentes_values.add(cv2);
                }
            }
            if(comprobaciones)
            {
                sgbd.insertarFicha(cv);
                sgbd.insertarComponentes(componentes_values);
                return true;
            }
        return false;

        }
    }

    /**
     * Compara fechas boolean.
     *
     * @param elaboracion the elaboracion
     * @param consumo     the consumo
     * @return the boolean
     */
    public boolean comparaFechas(String elaboracion, String consumo)
    {
        try
        {
            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
            Date consumopreferente = format.parse(consumo);
            Date Fechaelaboracion = format.parse(elaboracion);

            if (Fechaelaboracion.after(consumopreferente)) {
                return true;
            }
            return false;
        }
        catch (ParseException pe)
        {
            return true;
        }
    }

    /**
     * Vacio onulo boolean.
     *
     * @param campo the campo
     * @return the boolean
     */
    public boolean vacioOnulo(String campo){
        if(campo.isEmpty()||campo==null)
            return true;
        return false;
    }

    /**
     * Campo en bd boolean.
     *
     * @param campo the campo
     * @return the boolean
     */
    public boolean campoEnBD (String campo) { //Solo usar este método para comparar a la hora de almacenar un patrón
        ArrayList<String> listanombres = new ArrayList<String>();
        Boolean esta = false;
        //Inicializar ese objeto con los nombres que tengamos en la BD
        for(int i=0; i<listanombres.size() && !esta; i++)
        {
            if(listanombres.get(i).toLowerCase().trim().contentEquals(campo.toLowerCase().trim()))
                esta=true;
        }
        return esta;
    }

    /**
     * This method will be called when the user press on "Importar Patrón" button and will show in screen a lisf of (Radiobuttons) with the different names of the
     * Fichas storead as Patron in the DB.
     * To do this, it will be showed that list as an AlertDialog in which the user has to select what Patron he wants to import.
     * When the button OK is pressed, the activity will be populated with all the data of this Ficha imported as a Patron, so the user could modify this data to store a new Ficha.
     * Some fields are auto-generated like Date fields an Lote(ID) to avoid problems storing them in the DB.
     */
    public void importarPatron(){
        // setup the alert builder
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        // add a radio button list
        final ArrayList<String> listafichas=sgbd.getFichasPatron(); //Tomamos los nombres de las fichas almacenadas como patrón.
        if(listafichas.size()>0)
        {
            builder.setTitle("Escoge un patrón");

            String[] arrayfichas = new String[listafichas.size()];
            arrayfichas = listafichas.toArray(arrayfichas);


            builder.setSingleChoiceItems(arrayfichas, elementoclickado, new DialogInterface.OnClickListener() {
                //No podemos acceder a una variable desde una clase anónima a no ser que dicha variable haya sido declarada como GLOBAL.
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    elementoclickado=which;
                }
            });

            // add OK and Cancel buttons
            builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    popularFicha(elementoclickado ,listafichas);

                }
            });
            builder.setNegativeButton("Cancel", null);

            // create and show the alert dialog
            AlertDialog dialog = builder.create();
            dialog.show();
        }
        else
        {
            builder.setTitle("No hay patrones en la base de datos");


            // add OK and Cancel buttons
            builder.setPositiveButton("OK", null);

            // create and show the alert dialog
            AlertDialog dialog = builder.create();
            dialog.show();
        }

    }

    /**
     * Popular ficha.
     *
     * @param numelem     the numelem
     * @param listafichas the listafichas
     */
    public void popularFicha(int numelem, ArrayList<String> listafichas)
    { //Popula la ventana con los datos de la Ficha
        Ficha ficha=sgbd.getFichaByNombre(listafichas.get(numelem));
        ArrayList<Componente> listacomponentes=sgbd.getComponentesByFicha(ficha.getLote());
        if(ficha==null)
            System.out.println("Algo no ha ido como debería");
        else
        {
            TextView TVNombre = (TextView) findViewById(R.id.TVNombrePrep);
            TVNombre.setFocusable(false);
            TextView TVLote = (TextView) findViewById(R.id.TVLote);
            TVLote.setFocusable(false);
            TextView TVKg = (TextView) findViewById(R.id.TVKg);
            TextView TVFechaElab = (TextView) findViewById(R.id.SeleccionarCampos);
            TextView TVFechaConsum = (TextView) findViewById(R.id.TVFechaConsum);
            TVNombre.setText(ficha.getNombre());
            TVFechaConsum.setText(getNuevaFecha(ficha.getFecha_elaboracion(),ficha.getFecha_preferente()));
            TVKg.setText(ficha.getkg_Producto().toString());
            updateLabelPreferente();
            updateLabelElaboracion();
            TVLote.setText(nuevoLote(ficha.getLote()));
            LinearLayout layout_comp = (LinearLayout) findViewById(R.id.layout_componentes);
            TextView TVComNom = null;
            TextView TVComProv = null;
            TextView TVComLote = null;
            TextView TVComKg = null;
            while (componentes>0)
                ClickEliminar();
            for (Componente componente:listacomponentes) {
                ClickCrear();
                TVComNom = (TextView) layout_comp.findViewWithTag(("Nombre"+componentes));
                TVComProv = (TextView) layout_comp.findViewWithTag(("Proveedor"+componentes));
                TVComLote = (TextView) layout_comp.findViewWithTag(("Lote"+componentes));
                TVComKg = (TextView) layout_comp.findViewWithTag(("Peso"+componentes));
                TVComNom.setText(componente.getNombre());
                TVComProv.setText(componente.getProveedor());
                TVComLote.setText(componente.getLote());
                TVComKg.setText(componente.getKg_Componente().toString());
            }

        }

    }

    /**
     * Comprobar formato lote boolean.
     *
     * @param lote the lote
     * @return the boolean
     */
    public Boolean ComprobarFormatoLote(String lote){
        Pattern regex = Pattern.compile("^([A-Za-z]{4}[0-9]{5})$");
        Matcher m = regex.matcher(lote);
        return m.find();
    }

    /**
     * Nuevo lote string.
     *
     * @param lote the lote
     * @return the string
     */
    public String nuevoLote(String lote){
        String parteAlfabetica = lote.substring(0,4);
        String parteNumerica = null;
        String resultado=null;
        int aux=1;
        do {
            parteNumerica = String.format("%05d", Integer.parseInt(lote.substring(4,9))+aux);
            resultado=parteAlfabetica+parteNumerica.toString();
            aux++;
        }
        while (sgbd.loteRepetido(resultado));
        return resultado;
    }

    /**
     * Gets nueva fecha.
     *
     * @param fechaelab   the fechaelab
     * @param fechaconsum the fechaconsum
     * @return the nueva fecha
     */
    public String getNuevaFecha(String fechaelab, String fechaconsum) {
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yy", Locale.US);
        Calendar c = Calendar.getInstance();
        try {
            Date  dateelab = sdf.parse(fechaelab);
            Date dateconsum = sdf.parse(fechaconsum);
            long diff = dateconsum.getTime() - dateelab.getTime();
            int dias= ((int)(diff / (1000 * 60 * 60 * 24)));
            c.setTime(sdf.parse(fechaelab));
            c.add(Calendar.DATE, dias);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        return sdf.format(c.getTime());
    }

}

