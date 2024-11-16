package com.example.mysport

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import kotlinx.android.synthetic.main.activity_bmiview.*

class BMIView_Activity : AppCompatActivity() {
    /**
     * Třída pro vizualizaci výsledků výpočtu BMI z třídy BMICalculatorActivity.
     */

    /**
     * Nastavení layoutu, nastavení HomeButtonu v Toolbaru
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_bmiview)

        supportActionBar!!.setDisplayHomeAsUpEnabled(true)

        createResultView()
    }

    /**
     * Metoda pro vykreslení výsledku.
     * Získání výsledku bmi od BMICalculatorActivity
     */
    private fun createResultView() {
        val bmi = intent.getFloatExtra("Result", 0F)

        tvBMI.text = "Vaše BMI je: " + String.format("%.2f", bmi).toString()

        if (bmi < 18.5F) {
            tvBMI.append("; což je podváha!")
            tvTip.text =
                "Měli byste okamžitě začít správně jíst! Jinak to s vámi může špatně dopadnout!"
            imgBody.setImageResource(R.drawable.hubenour)
        } else if (bmi >= 18.5F && bmi < 24.99F) {
            tvBMI.append("; což je optimální váha!")
            tvTip.text = "Jde vidět, že děláte maximum pro své zdraví! Jen tak dál!"
            imgBody.setImageResource(R.drawable.normal)
        } else if (bmi >= 25F && bmi < 29.99F) {
            tvBMI.append("; což je trošku přes - mírná nadváha podle tabulek!")
            imgBody.setImageResource(R.drawable.nadvaha)
            tvTip.text = "Každopádně to určitě bude samý sval a šlacha!"
        } else if (bmi >= 30F && bmi < 34.99F) {
            tvBMI.append("; což je bohužel obezita 1. stupně!")
            tvTip.text =
                "Musíte se trošku více hlídat a hýbat. Co třeba začít používat schody místo výtahu :)."
            imgBody.setImageResource(R.drawable.stairs)
        } else if (bmi >= 35F && bmi < 39.99F) {
            tvBMI.append("; což je bohužel obezita 2. stupně!")
            tvTip.text =
                "Musíte se více hlídat a hýbat. Co třeba začít používat schody místo výtahu :)."
            imgBody.setImageResource(R.drawable.obesity)
        } else if (bmi >= 40F) {
            tvBMI.append("; tak to jsi sakra tlusťoch - obezita 3. stupně!")
            tvTip.text =
                "Raději si zajděte za výživovým poradcem."
            imgBody.setImageResource(R.drawable.obesity)
        }
    }
}