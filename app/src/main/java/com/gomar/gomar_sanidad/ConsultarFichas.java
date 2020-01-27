package com.gomar.gomar_sanidad;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import org.acra.ReportField;
import org.acra.ReportingInteractionMode;
import org.acra.annotation.ReportsCrashes;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Locale;
import java.util.regex.Pattern;

/**
 * The Activity ConsultarFichas will allow the user to select the fields to search for and will implement a top layer that will simplify the act of quering the DB.
 * This layer will be different depending on the type of the field to search for. The results of the search will be a List of Fichas and will be showed in ListaFichas Activity. That will be called from this Activity.
 * This class also implements mechanisms to avoid exceptions or errors due to bad values typed by the user.
 */

public class ConsultarFichas extends AppCompatActivity {
    /**
     * The Sgbd. An instance of SQL Class in order to be able to communicate with the DB.
     */
    private SQL sgbd = null;
    /**
     * The Selected items. It's global because it will be accesed from several methods.
     */
    private ArrayList<String> selectedItems = null;
    /**
     * The Seleccionados Boolean array is used to store information about the fields that the user has selected before.
     */
    private boolean[] seleccionados = null;
    /**
     * The Calendar elaboracion.
     */
    private final Calendar calendarElaboracion = Calendar.getInstance();
    /**
     * The Calendar consumo.
     */
    private final Calendar calendarConsumo = Calendar.getInstance();
    /**
     * The Text consumo.
     */
    private AutoCompleteTextView text_consumo = null;
    /**
     * The Text elaboracion.
     */
    private AutoCompleteTextView text_elaboracion = null;

    /**
     * The Actual Context. We have to create a Global variable with this because it will be used inside an anonymous class.
     */
    private Context actual=this;

    /**
     * First it will be initialized all the fields of the  tables in a way that will be showed to the user in order to not to have to change all of them (through all the Classes of this project)
     * if we have to redefine or change any of them. This functionality is introduced in order to respect the Separation into layers.
     */

    public static final String FechaElaboracionFichaCampo="Fecha Elaboración";
    public static final String FechaPreferenteFichaCampo="Fecha Consumo Preferente";
    public static final String KgFichaCampo="Kg Preparado";
    public static final String NombreFichaCampo="Nombre Preparado";
    public static final String LoteFichaCampo="Lote Preparado";
    public static final String PatronFichaCampo="lote_ficha";
    public static final String NombreComponenteCampo="Nombre Componente";
    public static final String LoteComponenteCampo="Lote Componente";
    public static final String ProveedorComponenteCampo="Proveedor";
    public static final String KGComponenteCampo="Kg Componente";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        sgbd = new SQL(this);
        setContentView(R.layout.activity_consultar_fichas);
        TextView seleccionarcampos = (TextView) findViewById(R.id.SeleccionarCampos);
        seleccionarcampos.setOnClickListener(btn_mostrarCampos);
        Button btn_efectuar_busqueda=(Button)findViewById(R.id.buttonBuscar);
        btn_efectuar_busqueda.setOnClickListener(btn_camposBusqueda);
       // creaPDF();


    }

    /**
     * The Btn mostrar campos.
     */
    public View.OnClickListener btn_mostrarCampos = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            mostrarCampos();
        }
    };
    /**
     * The Btn campos busqueda.
     */
    public View.OnClickListener btn_camposBusqueda = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            ClickBuscar();
        }
    };


    /**
     * This method will be called when the user press on "Lista campos a selecionar" button and will show in screen a lisf of (CheckBoxes) with the different names of the fields to search for.
     * To do this, it will be showed that list as an AlertDialog in which the user has to select the differents fields that he wants to search for.
     * The user can select several fields to search for. When the user press OK, it will be created some functionality in the activity in order to allow search by these field/s selected.
     */
    public void mostrarCampos() {
        // setup the alert builder
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Escoge los campos por lo que quieres buscar");

        // add a radio button list
        final ArrayList<String> listacampos =new ArrayList<>(sgbd.getCampos().keySet()); //Cogemos los nombres "BONITO" de los campos en la BD para mostrárselos al usuario.
        String[] campos = new String[listacampos.size()];
        campos = listacampos.toArray(campos);
        if (seleccionados == null)//Si no habíamos ejecutado la funcionalidad antes.
        {//Inicializamos el array a FALSE
            seleccionados = new boolean[listacampos.size()];
        }

//NOTA: El checkitems es un array de Booleans. Entiendo que tendrá que tener tantos elementos como el array de Strings.
        builder.setMultiChoiceItems(campos, seleccionados, new DialogInterface.OnMultiChoiceClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which,
                                boolean isChecked) {
                if (isChecked) {
                    // If the user checked the item, add it to the selected items
                    seleccionados[which] = true;

                } else if (selectedItems == null || selectedItems.contains(listacampos.get(which))) {

                    // Else, if the item is already in the array, remove it
                    seleccionados[which] = false;
                }
            }

        });

        // add OK and Cancel buttons
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                selectedItems = new ArrayList<String>();  // Where we track the selected items
                int i = 0;
                for (boolean elemento : seleccionados
                ) {
                    if (elemento) {
                        selectedItems.add(listacampos.get(i)); //Añadimos los nombres de los campos por los que el usuario quiere buscar.
                    }
                    i++;
                }
                LimpiarCamposBusqueda(); //Para limipar la actividad en caso de que haya habido más busquedas.
                ClickCrear(); //Llamada al método que poblará la actividad.

            }
        });
        builder.setNegativeButton("Cancel", null);

        // create and show the alert dialog
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    /**
     * This method will populate the activity with the needed functionality to search by the fields selected by the user.
     * Notice that this method try to emulate a layer to simplify the way to query the DB.
     * It will handle Two types of queries: Numeric and Date(adds a CheckList allowing the user to select >,<, =) and TEXT (normal way).
     */

    private void ClickCrear() {
        /*
        Notar que añado un Tag a todos los elementos que voy a tener que identificar cuando se haga click en el botón de "Buscar", pues me interesará conocer su valor.
         */
        final LinearLayout layout_comp = (LinearLayout) findViewById(R.id.layout_comp_busq);
        layout_comp.setOrientation(LinearLayout.VERTICAL);
        LinearLayout.LayoutParams LayoutParamsview = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        for (final String elemento : selectedItems) {

            TextView textViewComp = new TextView(this);
            textViewComp.setText("Campo " + elemento);
            textViewComp.setTypeface(textViewComp.getTypeface(), Typeface.BOLD);
            textViewComp.setLayoutParams(LayoutParamsview);
            layout_comp.addView(textViewComp);

            if (elemento.equals(KgFichaCampo) || elemento.equals(KGComponenteCampo) || elemento.equals(FechaPreferenteFichaCampo) || elemento.equals(FechaElaboracionFichaCampo)) {
               //Primera forma de búsqueda: Numérico y Fecha
                LinearLayout ll = new LinearLayout(this);
                ll.setTag(elemento+"Layout");
                CheckBox cb = new CheckBox(this);
                ll.setOrientation(LinearLayout.HORIZONTAL);
                CheckBox rbMayor = new CheckBox(this);
                rbMayor.setText("Mayor(>)");
                rbMayor.setTag(elemento + "Mayor");
                ll.addView(rbMayor);
                CheckBox rbMenor = new CheckBox(this);
                rbMenor.setText("Menor(<)");
                rbMenor.setTag(elemento + "Menor");
                ll.addView(rbMenor);
                CheckBox rbIgual = new CheckBox(this);
                rbIgual.setText("Igual(=)");
                rbIgual.setTag(elemento + "Igual");
                ll.addView(rbIgual);
                ll.setLayoutParams(LayoutParamsview);
                layout_comp.addView(ll);
                if (elemento.equals(KgFichaCampo) || elemento.equals(KGComponenteCampo)) {
                    AutoCompleteTextView text_Kg = new AutoCompleteTextView(this);
                    text_Kg.setHint(elemento + " por el que buscar");
                    text_Kg.setLayoutParams(LayoutParamsview);
                    text_Kg.setTag(elemento + "Text");
                    //Adding textview to linear layout using Add View function.
                    layout_comp.addView(text_Kg);
                } else if (elemento.equals(FechaPreferenteFichaCampo) || elemento.equals(FechaElaboracionFichaCampo)) {
                    /*
                    En caso de que sean Fechas tenemos que añadir funcionalidad adicional a sus TextViews, pues cuando se haga click en estos se mostrará un calendario.
                    Inicializado al día actual
                    */
                    if (elemento.equals(FechaElaboracionFichaCampo)) {
                        text_elaboracion = new AutoCompleteTextView(this);
                        text_elaboracion.setHint(elemento + " por el que buscar");
                        text_elaboracion.setLayoutParams(LayoutParamsview);
                        text_elaboracion.setTag(elemento + "Text");
                        final DatePickerDialog.OnDateSetListener dateElaboracion = new DatePickerDialog.OnDateSetListener() {

                            @Override
                            public void onDateSet(DatePicker view, int year, int monthOfYear,
                                                  int dayOfMonth) {
                                // TODO Auto-generated method stub
                                calendarElaboracion.set(Calendar.YEAR, year);
                                calendarElaboracion.set(Calendar.MONTH, monthOfYear);
                                calendarElaboracion.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                                updateLabel(text_elaboracion, calendarElaboracion);

                            }

                        };
                        text_elaboracion.setFocusable(false);
                        updateLabel(text_elaboracion, calendarElaboracion);
                        text_elaboracion.setOnClickListener(new View.OnClickListener() {


                            @Override
                            public void onClick(View v) {
                                // TODO Auto-generated method stub
                                new DatePickerDialog(ConsultarFichas.this, dateElaboracion, calendarElaboracion
                                        .get(Calendar.YEAR), calendarElaboracion.get(Calendar.MONTH),
                                        calendarElaboracion.get(Calendar.DAY_OF_MONTH)).show();
                                updateLabel(text_elaboracion, calendarElaboracion);
                            }
                        });
                        layout_comp.addView(text_elaboracion);

                    } else {
                        text_consumo = new AutoCompleteTextView(this);
                        text_consumo.setHint(elemento + " por el que buscar");
                        text_consumo.setLayoutParams(LayoutParamsview);
                        text_consumo.setTag(elemento + "Text");

                        final DatePickerDialog.OnDateSetListener dateConsumo = new DatePickerDialog.OnDateSetListener() {

                            @Override
                            public void onDateSet(DatePicker view, int year, int monthOfYear,
                                                  int dayOfMonth) {
                                // TODO Auto-generated method stub
                                calendarConsumo.set(Calendar.YEAR, year);
                                calendarConsumo.set(Calendar.MONTH, monthOfYear);
                                calendarConsumo.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                                updateLabel(text_consumo, calendarConsumo);
                            }
                        };

                        text_consumo.setFocusable(false);
                        updateLabel(text_consumo, calendarConsumo);
                        text_consumo.setOnClickListener(new View.OnClickListener() {

                            @Override
                            public void onClick(View v) {
                                // TODO Auto-generated method stub
                                new DatePickerDialog(ConsultarFichas.this, dateConsumo, calendarConsumo
                                        .get(Calendar.YEAR), calendarConsumo.get(Calendar.MONTH),
                                        calendarConsumo.get(Calendar.DAY_OF_MONTH)).show();
                                updateLabel(text_consumo, calendarConsumo);
                            }
                        });
                        layout_comp.addView(text_consumo);
                    }
                }

            } else {
                //Segunda forma de buscar, por campo de texto (Búsqueda normal)
                AutoCompleteTextView auto_nombre = new AutoCompleteTextView(this);
                auto_nombre.setHint(elemento + " por el que buscar");
                auto_nombre.setLayoutParams(LayoutParamsview);
                auto_nombre.setTag(elemento + "Text");
                /*
                Añadimos a los campos de texto un Listener para que se ejecute una llamada al método busquedaLike de la BD cada vez que el usuario escriba algo en estos campos.
                Notar también como se utiliza el método getCampos de la BD para mapear los campos "BONITOS" (que se le muestran al usuario) con los nombres reales de los campos en la BD.
                 */
                auto_nombre.addTextChangedListener(new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                    }

                    @Override
                    public void onTextChanged(CharSequence s, int start, int before, int count) {
                            ArrayList<String> listanombres=sgbd.busquedaLike(sgbd.getCampos().get(elemento),"",s.toString());
                        ArrayAdapter<String> adapter = new ArrayAdapter<String>
                                (actual, android.R.layout.select_dialog_item, listanombres.toArray(new String[0]));
                        AutoCompleteTextView actv = (AutoCompleteTextView) layout_comp.findViewWithTag(elemento + "Text");
                        actv.setThreshold(1);//will start working from first character
                        actv.setAdapter(adapter);//setting the adapter data into the AutoCompleteTextView
                    }

                    @Override
                    public void afterTextChanged(Editable s) {

                    }
                });
                //Adding textview to linear layout using Add View function.
                layout_comp.addView(auto_nombre);
            }
        }
    }


    private void updateLabel(AutoCompleteTextView text_fecha, Calendar cal) {
        /*
        Aquí cogeremos la primera fecha de consumo preferente que se introdujo para la creación de la ficha patrón, calcularemos los días desde el día actual
        hasta dicha fecha y siempre que generemos una ficha similar le sumaremos esos días con respecto al día de creación del elaborado.
         */
        String myFormat = "dd/MM/yy"; //In which you need put here
        SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);
        text_fecha.setText(sdf.format(cal.getTime()));

    }

    /**
     * This method will remove every Search element created in the Activity to populate again that Activity with the new fields to search.
     */
    public void LimpiarCamposBusqueda() {
        LinearLayout layout_comp = (LinearLayout) findViewById(R.id.layout_comp_busq);
        layout_comp.removeAllViews();
    }

    /**
     * When the search button is pressed by the user (Button BUSCAR) it will call the Lista_Fichas Activity to show the search results.
     * In the call to this method, it will be executed several checks to see if the data typed by the user is correct.
     * @return A boolean that only serve as a flow control and is not used.
     */
    public boolean ClickBuscar(){
        if(selectedItems.size()>0)
        {
            ArrayList<Condicion> condiciones=GetFieldsToSearch();
            if(condiciones!=null)
            {
                /*
                Le pasamos la lista de condiciones generadas al método de la BD que construye la query y la ejecuta. Este método devuelve la lista de fichas resultado de la búsqueda.
                Esta lista de fichas es pasada como parámetro a la nueva actividad generada (Lista_Fichas).
                 */
                sgbd.constructorDeCondiciones(condiciones);
                Intent intent = new Intent(this, Lista_Fichas.class);
                intent.putStringArrayListExtra("listafichas",sgbd.constructorDeCondiciones(condiciones));
                startActivity(intent);
                // Do something in response to button
                return true;
            }
            return false;
        }
        Toast.makeText(this, "No has introducido ningún campo para buscar", Toast.LENGTH_SHORT).show();
        return false;
    }

    /**
     * This method will read the different information typed by the user in the Activity and will generate an ArrayList of Condtitions(Condiciones).
     * This method will also check if the values typed by the user are valid and if not, it will print a message on the screen warning that and will return NULL.
     * @return A ArrayList that contain all the conditions (Objects Condicion) typed by the user.
     */
    private ArrayList<Condicion> GetFieldsToSearch() {
        HashMap<String,String> campos_BD_bonitos = sgbd.getCampos();
        ArrayList<Condicion> condiciones = new ArrayList<Condicion>();
        LinearLayout layout_comp = (LinearLayout) findViewById(R.id.layout_comp_busq);
        String campo=null;
        String valor = null;
        String criterio =null;
        for (String elemento : selectedItems) {
            Boolean numerico =false; //Para evitar que cree la condición del WHERE como String.
            campo=campos_BD_bonitos.get(elemento);
            valor=((AutoCompleteTextView)layout_comp.findViewWithTag((elemento + "Text"))).getText().toString();
            if (elemento.equals(KgFichaCampo) || elemento.equals(KGComponenteCampo) || elemento.equals(FechaPreferenteFichaCampo) || elemento.equals(FechaElaboracionFichaCampo)) {
                Boolean mayor=false;
                Boolean menor=false;
                Boolean igual=false;
                LinearLayout layout_checkbox=(LinearLayout)layout_comp.findViewWithTag(elemento+"Layout");
                /*final int childCount = layout_checkbox.getChildCount();
                for (int i = 0; i < childCount; i++) {
                    View v = layout_checkbox.getChildAt(i);
                }*/
               if(((CheckBox)layout_checkbox.findViewWithTag(elemento + "Mayor")).isChecked()){
                   mayor=true;
               }
               if(((CheckBox)layout_checkbox.findViewWithTag(elemento + "Menor")).isChecked())
               {
                   menor=true;
               }
               if((((CheckBox)layout_checkbox.findViewWithTag(elemento + "Igual")).isChecked()))
               {
                   igual=true;
               }
               if(!mayor && !menor && !igual)
               {
                   Toast.makeText(this, "Tienes que seleccionar al menos una de las casillas  \"Mayor\", \"Menor\" y/o \"Igual\" para el campo "+elemento, Toast.LENGTH_SHORT).show();
                   return null;
               }
               if(mayor && menor)
               {
                   Toast.makeText(this, "No puedes seleccionar a la vez las casillas de \"Mayor\" y \"Menor\" para el campo "+elemento, Toast.LENGTH_SHORT).show();
                   return null;

               }
               else if(mayor && igual)
               {
                    criterio=">=";
               }
               else if(menor && igual){
                   criterio="<=";
               }
               else if(igual)
               {
                   criterio="=";
               }
               else if(mayor)
               {
                   criterio=">";
               }
               else if(menor)
               {
                   criterio="<";
               }
               //Validar campo numérico.
                if(elemento.equals(KgFichaCampo) || elemento.equals(KGComponenteCampo))
                {
                    try {
                        Double.parseDouble(valor);
                        numerico=true;
                    } catch (NumberFormatException e) {
                        Toast.makeText(this, "Has introducido un tipo no numérico (Solo números y decimales separados por punto) para el campo "+elemento, Toast.LENGTH_SHORT).show();
                        return null;

                    }
                }
            }
            else
            {
                //Validar campo formado por solo letras.
                criterio="=";
                Pattern pattern = Pattern.compile("[A-Za-z0-9_\\-\\s]*");
                if (valor.trim().isEmpty() || !pattern.matcher(valor).matches()) {
                    Toast.makeText(this, "No puedes introducir caracteres diferentes de: cualquier letra, digíto, espacio en blanco, '_' o '-' para el campo "+elemento, Toast.LENGTH_SHORT).show();
                    return null;

                }
            }
            condiciones.add(new Condicion(campo,criterio,valor,numerico));

        }
        return condiciones;
    }

}
