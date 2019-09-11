package com.example.informatika;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;

public class TasksFragment extends Fragment {
    ArrayList<ListClass> products = new ArrayList<ListClass>();
    String[] nameTasks = {
            "Кодирование и операции над числами",
            "Таблицы истинности",
            "Анализ информационных моделей",
            "Базы данных. Файловая система",
            "Кодирование и декодирование информации",
            "Алгоритмы для исполнителей",
            "Анализ диаграмм и электронных таблиц",
            "Анализ программ",
            "Передача информации",
            "Перебор слов и системы счисления",
            "Рекурсивные алгоритмы",
            "Организация компьютерных сетей",
            "Вычисление количества информации",
            "Исполнитель РОБОТ",
            "Поиск путей в графе",
            "Кодирование чисел. Системы счисления",
            "Поисковые системы",
            "Преобразование логических выражений",
            "Обработка массивов и матриц",
            "Программы с циклами",
            "Анализ программ с циклами",
            "Присваивание, ветвление, построение дерева",
            "Логические уравнения"};

    @Override
    public View onCreateView(final LayoutInflater inflater, final ViewGroup container, Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_tasks, container, false);

        fillData();
        BoxAdapter boxAdapter = new BoxAdapter(getContext(), products);

        ListView lvMain = view.findViewById(R.id.list_tasks);
        lvMain.setAdapter(boxAdapter);

        lvMain.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view1, int position, long id) {
                /*Intent intent = new Intent(getActivity(), TasksPoolActivity.class);
                intent.putExtra("text_pool_name", nameTasks[position]);
                intent.putExtra("text_pool_id", position + 1 + "");
                startActivity(intent);*/

                Intent intent = new Intent(getActivity(), TasksPoolActivity.class);
                intent.putExtra("text_pool_name", nameTasks[position]);
                intent.putExtra("text_pool_id", position + 1 + "");
                startActivity(intent);
                getActivity().overridePendingTransition(R.anim.translate_show, R.anim.translate_hide);
            }
        });

        return view;
    }

    void fillData() {
        try{
            String[] strPercent = new String[nameTasks.length];
            String path = getContext().getFilesDir() + "/tasks_percents.txt";       // Чтение файла с прогрессом заданий
            File file = new File(path);

            if (!file.exists()) {
                file.createNewFile();
            }

            BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(file)));
            String line = br.readLine();
            br.close();

            if (line == null) {
                Toast toast = Toast.makeText(getContext(), "Файл пуст", Toast.LENGTH_LONG);
                toast.show();

                FileWriter fw = new FileWriter(path);
                for (int i = 0; i < nameTasks.length; i++) {
                    strPercent[i] = "0";
                    fw.write(strPercent[i] + ";");
                }
                fw.flush();
                fw.close();
            } else {
                String[] str = line.split(";");

                for (int i = 0; i < nameTasks.length; i++) {
                    strPercent[i] = str[i];
                }
            }
            for (int i = 0; i < nameTasks.length; i++) {
                int id = i + 1;
                String[] count = getActivity().getAssets().list("tasks/task_" + id);

                if (Integer.valueOf(strPercent[i]) != 0) {
                    int percent = Integer.valueOf(strPercent[i]) * 100 / count.length;
                    strPercent[i] = Integer.toString(percent);
                }
                products.add(new ListClass(i + 1, nameTasks[i], Integer.parseInt(strPercent[i])));
            }
        } catch (IOException ex) {
            Toast toast = Toast.makeText(getContext(), ex.getMessage(), Toast.LENGTH_LONG);
            toast.show();
        } catch (Exception ex) {
            Toast toast = Toast.makeText(getContext(), ex.getMessage(), Toast.LENGTH_LONG);
            toast.show();
        }


    }

}