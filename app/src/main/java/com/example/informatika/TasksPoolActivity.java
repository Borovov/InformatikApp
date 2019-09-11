package com.example.informatika;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.InputType;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public class TasksPoolActivity extends AppCompatActivity {
    String answer;
    String keyId;
    String keyName;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_task_pool);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().setStatusBarColor(getResources().getColor(R.color.mainBlue));
        }
        LinearLayout linear = findViewById(R.id.layout_pool);

        keyName = getIntent().getExtras().getString("text_pool_name");
        keyId = getIntent().getExtras().getString("text_pool_id");

        setToolBar(keyName);
        setTask(linear, keyId);
    }

    private void setToolBar(String key) {
        Toolbar toolbar = findViewById(R.id.toolbar_pool);
        TextView text = toolbar.findViewById(R.id.text_bar_pool);
        text.setText(key);

        Button button = toolbar.findViewById(R.id.but_back_pool);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
    }

    public void setTask(LinearLayout linear, String keyId) {
        try {
            // Чтение файла с позициями заданий
            BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(
                    getFilesDir() + "/tasks_percents.txt")));
            String[] tasksPos = br.readLine().split(";");
            br.close();

            // Чтение файла задания
            int taskNum = Integer.valueOf(tasksPos[Integer.valueOf(keyId) - 1]) + 1;
            InputStream stream = getAssets().open("tasks/task_" + keyId + "/" + taskNum + "/text.txt");
            byte[] array = new byte[stream.available()];
            stream.read(array);
            stream.close();

            // Файл разбивается на части для дальнейшего создания разметки
            String[] taskParts = new String(array).split(";");
            answer = taskParts[0];
            setTaskStructure(taskNum, taskParts, linear, keyId);
        } catch (IOException ex) {
            Toast toast = Toast.makeText(getApplicationContext(), ex.getMessage(), Toast.LENGTH_SHORT);
            toast.show();
        }
    }

    public void setTaskLayout(int taskNum, String[] taskParts, LinearLayout linear, String keyId) {
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
                        isImg = getAssets().open("tasks/task_" + keyId + "/" + taskNum + "/img" + imgCount + ".png");
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
                        isCode = getAssets().open("tasks/task_" + keyId + "/" + taskNum + "/code" + codeCount + ".txt");
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
                    tv.setText("\t" + taskParts[i]);
                    linear.addView(tv, lpContent);
                    break;
            }
        }
    }

    public void setAnsw(final LinearLayout linearLayout) {
        Button but = new Button(this);
        but.setBackgroundResource(R.drawable.shape_butrezult);
        final EditText editText = new EditText(this);

        but.setText("Ответить");
        but.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    String inputStr = editText.getText().toString();

                    if (inputStr.length() != 0) {
                        if (answer.equals(inputStr)) {
                            String path = getFilesDir() + "/tasks_percents.txt";
                            BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(path)));
                            String[] percents = br.readLine().split(";");
                            br.close();

                            int num = Integer.valueOf(percents[Integer.valueOf(keyId) - 1]) + 1;
                            percents[Integer.valueOf(keyId) - 1] = Integer.toString(num);

                            FileWriter fw = new FileWriter(path);
                            for (int i = 0; i < percents.length; i++) {
                                fw.write(percents[i] + ";");
                            }
                            fw.flush();
                            fw.close();

                            showSucces(true, editText);
                        } else {
                            showSucces(false, editText);
                        }
                    } else {
                        Toast toast = Toast.makeText(getApplicationContext(), "Поле ввода пусто", Toast.LENGTH_SHORT);
                        toast.show();
                    }
                } catch (IOException e) {
                        Toast toast = Toast.makeText(getApplicationContext(), "Исходные файлы повреждены", Toast.LENGTH_SHORT);
                        toast.show();
                } catch (NumberFormatException nfe) {
                    Toast toast = Toast.makeText(getApplicationContext(), "Неудаётся преобразовать входные данные", Toast.LENGTH_SHORT);
                    toast.show();
                }
            }
        });

        LinearLayout.LayoutParams lParam = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT);
        lParam.setMargins(20, 20, 20, 10);

        linearLayout.addView(editText, lParam);
        linearLayout.addView(but, lParam);
    }

    private void showSucces(final boolean succes, EditText editText) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setCancelable(false);
        editText.setEnabled(false);

        if (succes) {
            builder.setTitle("Задание решено верно");
        } else {
            builder.setTitle("В решении имеются ошибки");
        }

        builder.setPositiveButton("Следующее задание", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                Intent intent = new Intent(getBaseContext(), TasksPoolActivity.class);
                intent.putExtra("text_pool_name", keyName);
                intent.putExtra("text_pool_id", keyId);
                startActivity(intent);
                overridePendingTransition(R.anim.translate_show, R.anim.translate_hide);
                finish();
                if (!succes) {
                    try {
                        String path = getFilesDir() + "/tasks_percents.txt";
                        BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(path)));
                        String[] percents = br.readLine().split(";");
                        br.close();

                        int num = Integer.valueOf(percents[Integer.valueOf(keyId) - 1]) + 1;
                        percents[Integer.valueOf(keyId) - 1] = Integer.toString(num);

                        FileWriter fw = new FileWriter(path);
                        for (int i = 0; i < percents.length; i++) {
                            fw.write(percents[i] + ";");
                        }
                        fw.flush();
                        fw.close();
                    } catch (IOException ex) {
                        Toast toast = Toast.makeText(getApplicationContext(), "Исходные файлы повреждены", Toast.LENGTH_SHORT);
                        toast.show();
                    }
                }
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

        if (!succes) {
            butPositive.setText("Отложить задание");
            butNegative.setText("Попробовать заново");
            editText.setEnabled(true);
        }
    }

    // Передаём номер задания, составные части задания и разметку.
    public void setTaskStructure(int taskNum, String[] taskParts, LinearLayout linear, String keyId) {
        setTaskLayout(taskNum, taskParts, linear, keyId);
        setAnsw(linear);
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
