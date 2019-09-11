package com.example.informatika;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Random;

public class VariantPoolActivity extends AppCompatActivity {
    String[] answer = new String[23];

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_variant_pool);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().setStatusBarColor(getResources().getColor(R.color.mainBlue));
        }
        setToolbar();
        setTaskList();
    }

    private void setToolbar() {
        Button butBack = findViewById(R.id.but_back_pool);
        butBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
    }

    private void setTaskList() {
        LinearLayout linearMain = findViewById(R.id.layout_variant_pool);
        try {
            String[] count = getAssets().list("tasks");
            final EditText[] textes = new EditText[count.length];
            final LinearLayout[] layouts = new LinearLayout[count.length];
            boolean check = false;

            for (int i = 1; i < count.length + 1; i++) {
                LinearLayout linear = new LinearLayout(this);
                LinearLayout.LayoutParams lpLinear = new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT);
                lpLinear.setMargins(20, 20, 20, 0);
                linear.setLayoutParams(lpLinear);
                linear.setOrientation(LinearLayout.VERTICAL);
                Random rand = new Random();
                int taskNum = rand.nextInt(getAssets().list("tasks/task_" + i).length) + 1;

                InputStream stream = getAssets().open("tasks/task_" + i + "/" + taskNum + "/text.txt");
                byte[] array = new byte[stream.available()];
                stream.read(array);
                stream.close();

                String[] taskParts = new String(array).split(";");
                answer[i - 1] = taskParts[0];       // Запись ответа в массив ответов
                layouts[i - 1] = setTaskListLayout(linear, Integer.toString(i), taskNum, taskParts);    // Установка тела задания
                textes[i - 1] = setListEdit(linear);    // Установка полей ввода на каждом из заданий
                textes[i - 1].setText(taskParts[0]);

                linearMain.addView(linear);
            }
            linearMain.addView(setListBut(layouts, textes, check));    // Добавление в конец кнопки проверки результата
        } catch (IOException ex) {
            Toast toast = Toast.makeText(getApplicationContext(), ex.getMessage(), Toast.LENGTH_LONG);
            toast.show();
        }
    }

    private LinearLayout setTaskListLayout(LinearLayout linear, String taskId, int taskNum, String[] taskParts) {
        LinearLayout.LayoutParams lpContent = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT);
        int imgCount = 0, codeCount = 0;

        for (int i = 1; i < taskParts.length; i++) {
            switch (taskParts[i]) {
                case "img":
                    imgCount++;
                    ImageView iv = new ImageView(this);
                    InputStream isImg = null;
                    try {
                        isImg = getAssets().open("tasks/task_" + taskId + "/" + taskNum + "/img" + imgCount + ".png");
                        Drawable d = Drawable.createFromStream(isImg, null);
                        isImg.close();
                        iv.setImageDrawable(d);
                        linear.addView(iv, lpContent);
                    } catch (IOException e) {
                        Toast toast = Toast.makeText(getApplicationContext(), "Не удаётся открыть изображение.", Toast.LENGTH_SHORT);
                        toast.show();
                    }
                    break;

                case "code":
                    codeCount++;
                    InputStream isCode = null;
                    try {
                        isCode = getAssets().open("tasks/task_" + taskId + "/" + taskNum + "/code" + codeCount + ".txt");
                        byte[] array = new byte[isCode.available()];
                        isCode.read(array);
                        isCode.close();

                        String[] codes = new String(array).split("@");
                        LinearLayout.LayoutParams linearParams = new LinearLayout.LayoutParams(
                                LinearLayout.LayoutParams.MATCH_PARENT,
                                LinearLayout.LayoutParams.WRAP_CONTENT);
                        linearParams.setMargins(0, 0, 20, 0);
                        LinearLayout.LayoutParams codeParams = new LinearLayout.LayoutParams(
                                LinearLayout.LayoutParams.WRAP_CONTENT,
                                LinearLayout.LayoutParams.MATCH_PARENT, 1);
                        codeParams.setMargins(20, 20, 0, 0);

                        for (int n = 0; n < (codes.length + 1) / 2; n++) {
                            LinearLayout linearHor = new LinearLayout(this);
                            linearHor.setOrientation(LinearLayout.HORIZONTAL);
                            linearHor.setLayoutParams(linearParams);

                            for (int m = 0; m < 2 && codes.length != n * 2 + m; m++) {
                                TextView text = new TextView(this);
                                text.setText(codes[n * 2 + m]);
                                text.setPadding(20, 10, 10 ,15);
                                text.setBackgroundResource(R.drawable.shape_code);
                                text.setTextColor(Color.BLACK);
                                text.setLayoutParams(codeParams);
                                linearHor.addView(text);
                            }
                            linear.addView(linearHor);
                        }
                    } catch (IOException e) {
                        Toast toast = Toast.makeText(getApplicationContext(), "Не удаётся открыть блок кода.", Toast.LENGTH_SHORT);
                        toast.show();
                    }
                    break;

                default:
                    TextView tv = new TextView(this);
                    tv.setPadding(20, 20, 5 ,0);
                    tv.setTextSize(18);
                    tv.setText("\t"+ taskId + "." + taskParts[i]);
                    tv.setTextColor(getResources().getColor(R.color.mainText));
                    linear.addView(tv, lpContent);
                    break;
            }
        }
        linear.setBackgroundResource(R.drawable.shape_var);
        return linear;
    }

    public EditText setListEdit(LinearLayout linearLayout) {
        EditText editText = new EditText(this);
        LinearLayout.LayoutParams lpEdit = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT);
        lpEdit.setMargins(20, 20, 20, 20);
        editText.setLayoutParams(lpEdit);
        //editText.setBackground(getResources().getDrawable(R.drawable.edittext_theme));
        /*editText.setOnFocusChangeListener( new View.OnFocusChangeListener(){
            public void onFocusChange( View view, boolean hasfocus){
                if(hasfocus){
                    view.setBackgroundResource(R.drawable.shape);
                } else{
                    view.setBackgroundResource(R.drawable.shape_green);
                }
            }
        });*/
        linearLayout.addView(editText);
        return editText;
    }

    public Button setListBut(final LinearLayout[] layouts, final EditText[] textes, final boolean check) {
        final String path = getFilesDir() + "/variant_progress.txt";
        final File file = new File(path);
        final Button butAnsw = new Button(this);
        butAnsw.setBackgroundResource(R.drawable.shape_butrezult);
        LinearLayout.LayoutParams lpButAnsw = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT);
        lpButAnsw.setMargins(20, 20, 20, 20);
        butAnsw.setLayoutParams(lpButAnsw);
        butAnsw.setPadding(20, 0, 20, 0);
        butAnsw.setText("Подвести итоги");

        butAnsw.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                boolean succes = true;
                int countSuc = 0;

                // Проверка полей ввода на пустоту
                for (int i = 0; i < textes.length; i++) {
                    String inputAnsw = textes[i].getText().toString();

                    if (inputAnsw.length() == 0) {
                        Toast toast = Toast.makeText(getApplicationContext(), "Есть незаполненные поля", Toast.LENGTH_LONG);
                        toast.show();
                        return;
                    }
                }
                // Определение правильности введённых ответов
                for (int i = 0; i < textes.length; i++) {
                    String inputAnsw = textes[i].getText().toString();
                    String[] input = inputAnsw.split(" ");
                    inputAnsw = input[0];
                    textes[i].setEnabled(false);

                    if (answer[i].equals(inputAnsw)) {
                        layouts[i].setBackgroundResource(R.drawable.shape_green);
                        countSuc++;
                    } else {
                        layouts[i].setBackgroundResource(R.drawable.shape_red);
                        textes[i].setText(inputAnsw + " (Ответ: " + answer[i] + ")");
                        succes = false;
                    }
                }

                if (butAnsw.getText().equals("Подвести итоги")) {
                    try {
                        if (!file.exists()) {
                            file.createNewFile();
                        }
                        BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(file)));   // Чтение имеющегося прогресса
                        String line = br.readLine();
                        Float num, sum;
                        int count;

                        if (line == null) {
                            FileWriter fw = new FileWriter(file);
                            fw.write("0;0;");
                            fw.flush();
                            fw.close();
                            line = br.readLine();
                        }
                        br.close();

                        String[] percents = line.split(";");

                        num = Float.valueOf(percents[0]);       // Среднее значение
                        count = Integer.valueOf(percents[1]);   // Количество чисел
                        sum = num * count;                      // Среднее значение умножаем на количество чисел
                        count++;                                // Инкремент количества чисел
                        num = (sum + countSuc) / count;         // (сумма значений + новое значение) / количество значений

                        FileWriter fw = new FileWriter(file);   // Запись результата решённого варианта
                        fw.write(num + ";");
                        fw.write(Integer.toString(count));
                        fw.flush();
                        fw.close();
                    } catch (IOException ex) {
                        Toast toast = Toast.makeText(getApplicationContext(), ex.getMessage(), Toast.LENGTH_LONG);
                        toast.show();
                    }

                    butAnsw.setText("Итоги");
                }
                showSucces(succes, countSuc, textes.length);
            }
        });

        return butAnsw;
    }

    private void showSucces(boolean succes, int countSuc, int countAll) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setCancelable(false);

        if (succes) {
            builder.setTitle("Вариант решён без ошибок");
        } else {
            builder.setTitle("Итоги");
        }

        builder.setMessage("Решено правильно " + countSuc + "/" + countAll + " заданий");
        builder.setPositiveButton("Следующий вариант", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        Intent intent = new Intent(getBaseContext(), VariantPoolActivity.class);
                        startActivity(intent);
                        overridePendingTransition(R.anim.translate_show, R.anim.translate_hide);
                        finish();
                    }
                });
        builder.setNegativeButton("Просмотр результата", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                dialog.cancel();
            }
        });
        builder.setNeutralButton("Выход", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        finish();
                        overridePendingTransition(R.anim.translate_show_left, R.anim.translate_hide_left);
                    }
                });

        AlertDialog dialog = builder.create();
        dialog.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation;
        dialog.getWindow().setBackgroundDrawableResource(R.drawable.shape_dialog);
        dialog.show();

        Button butPositive = dialog.getButton(DialogInterface.BUTTON_POSITIVE);
        Button butNegative = dialog.getButton(DialogInterface.BUTTON_NEGATIVE);
        Button butNeutral = dialog.getButton(DialogInterface.BUTTON_NEUTRAL);

        butPositive.setTextColor(getResources().getColor(R.color.midblue));
        butNegative.setTextColor(getResources().getColor(R.color.mainText));
        butNeutral.setTextColor(getResources().getColor(R.color.midgrey));
    }

    @Override
    public void onBackPressed() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        builder.setMessage("Вы действительно хотите выйти?");
        builder.setPositiveButton("Продолжить", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                dialog.cancel();
            }
        });
        builder.setNegativeButton("Выход", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                Intent intent = new Intent(getBaseContext(), VariantFragment.class);
                finish();
                overridePendingTransition(R.anim.translate_show_left, R.anim.translate_hide_left);
            }
        });

        AlertDialog dialog = builder.create();
        dialog.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation;
        dialog.show();

        Button butPositive = dialog.getButton(DialogInterface.BUTTON_POSITIVE);
        Button butNegative = dialog.getButton(DialogInterface.BUTTON_NEGATIVE);
        butPositive.setTextColor(getResources().getColor(R.color.midblue));
        butNegative.setTextColor(getResources().getColor(R.color.midgrey));

        dialog.getWindow().setBackgroundDrawableResource(R.drawable.shape_dialog);
    }
}
