package com.example.json

import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.google.gson.Gson
import com.google.gson.JsonSyntaxException

class MainActivity : AppCompatActivity() {

    private val gson = Gson()

    private lateinit var etName: EditText
    private lateinit var etPrice: EditText
    private lateinit var etTags: EditText
    private lateinit var tvResult: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        etName = findViewById(R.id.etName)
        etPrice = findViewById(R.id.etPrice)
        etTags = findViewById(R.id.etTags)
        tvResult = findViewById(R.id.tvResult)

        val btnManual: Button = findViewById(R.id.btnManual)
        val btnFromInput: Button = findViewById(R.id.btnFromInput)
        val btnCheck: Button = findViewById(R.id.btnCheck)

        btnManual.setOnClickListener {
            val product = Product(
                name = "Программирование на Kotlin",
                price = 1500.0,
                tags = listOf("учебник", "программирование", "android")
            )
            process(product)
        }

        btnFromInput.setOnClickListener {
            val name = etName.text.toString().trim()
            val price = etPrice.text.toString().replace(',', '.').toDoubleOrNull()

            if (name.isEmpty() || price == null) {
                tvResult.text = "Введите название и корректную цену"
                return@setOnClickListener
            }

            val tags = etTags.text.toString()
                .split(",")
                .map { it.trim() }
                .filter { it.isNotEmpty() }

            process(Product(name, price, tags))
        }

        btnCheck.setOnClickListener { selfCheck() }
    }

    private fun process(product: Product) {
        val json: String = gson.toJson(product)                       // сериализация
        val restored: Product = gson.fromJson(json, Product::class.java) // десериализация

        val text = "JSON:\n$json\n\n" +
                "После десериализации:\n" +
                "Название: ${restored.name}\n" +
                "Цена: ${restored.price}\n" +
                "Теги: ${restored.tags.joinToString(", ")}"

        tvResult.text = text
        Log.d("JsonApp", text)
    }

    // Ошибки для самопроверки
    private fun selfCheck() {
        // 1. В JSON нет поля price
        val noPrice = """{"name":"Книга","tags":["a","b"]}"""

        // 2. В JSON есть лишнее поле discount, которого нет в классе
        val extraField = """{"name":"Книга","price":100.0,"tags":["a"],"discount":5}"""

        // 3. Тип не совпадает: цена пришла строкой, не число
        val wrongType = """{"name":"Книга","price":"abc","tags":["a"]}"""

        val sb = StringBuilder()
        sb.append("1) Нет price:\n").append(tryParse(noPrice)).append("\n\n")
        sb.append("2) Лишнее поле:\n").append(tryParse(extraField)).append("\n\n")
        sb.append("3) price = \"abc\":\n").append(tryParse(wrongType))

        tvResult.text = sb.toString()
        Log.d("JsonApp", sb.toString())
    }

    private fun tryParse(json: String): String {
        return try {
            val p = gson.fromJson(json, Product::class.java)
            "OK: name=${p.name}, price=${p.price}, tags=${p.tags}"
        } catch (e: Exception) {
            "ОШИБКА: ${e.javaClass.simpleName}: ${e.message}"
        }
    }
}