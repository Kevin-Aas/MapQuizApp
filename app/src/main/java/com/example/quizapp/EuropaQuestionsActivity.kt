package com.example.quizapp

import android.content.Intent
import android.graphics.Color
import android.graphics.Typeface
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat
import kotlinx.android.synthetic.main.activity_quiz_questions.*
import android.content.res.Resources
import android.media.MediaPlayer
import android.net.Uri
import java.lang.reflect.Field
import java.util.*
import kotlin.collections.ArrayList
import kotlin.random.Random

class EuropaQuestionsActivity : AppCompatActivity(), View.OnClickListener {

    private var mCurrentPosition:Int = 1
    private var mQuestionsList: ArrayList<Question>? = null
    private var mSelectedOptionPosition:Int = 0
    private var mCorrectAnswers: Int = 0
    private var mUserName: String? = null

    val europa = listOf<String>("Albania", "Andorra", "Østerrike",
        "Hviterussland", "Belgia", "Bosnia og Herzegovina", "Bulgaria", "Kroatia", "Kypros",
        "Tsjekkia", "Danmark", "Estland", "Finland", "Frankrike", "Tyskland", "Hellas",
        "Ungarn", "Island", "Irland", "Italia", "Kosovo", "Latvia", "Liechtenstein",
        "Litauen", "Luxembourg", "Malta", "Moldova", "Monaco", "Montenegro", "Nederland",
        "Nord-Makedonia", "Norge", "Polen", "Portugal", "Romania", "Russland", "San Marino",
        "Serbia", "Slovakia", "Slovenia", "Spania", "Sverige", "Sveits", "Tyrkia", "Ukraina",
        "Storbritannia", "Vatikanstaten")

    val europa_draw = mutableListOf<String>("albania", "andorra", "austria",
        "belarus", "belgium", "bosnia_herzegovina", "bulgaria", "croatia", "cyprus",
        "czech_republic", "denmark", "estonia", "finland", "france", "germany", "greece",
        "hungary", "iceland", "ireland", "italy", "kosovo", "latvia", "liechtenstein",
        "lithuania", "luxembourg", "malta", "moldova", "monaco", "montenegro", "netherlands",
        "north_macedonia", "norway", "poland", "portugal", "romania", "russia", "san_marino",
        "serbia", "slovakia", "slovenia", "spain", "sweden", "switzerland", "turkey", "ukraine",
        "united_kingdom", "vatican_city")


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_quiz_questions)

        for (i in (0 until (europa_draw.size))){
            europa_draw[i] = "flag_of_" + europa_draw[i]
        }

        mQuestionsList = getQuestions()

        setQuestion()

        tv_option_one.setOnClickListener(this)
        tv_option_two.setOnClickListener(this)
        tv_option_three.setOnClickListener(this)
        tv_option_four.setOnClickListener(this)
        btn_submit.setOnClickListener(this)

    }

    private fun setQuestion(){
        val currentQuestion = mQuestionsList!![mCurrentPosition - 1]

        defaultOptionsView()
        defaultSubmitView()

        if(mCurrentPosition == mQuestionsList!!.size){
            btn_submit.text = "Ferdig"
        }else{
            btn_submit.text = "Neste spørsmål"
        }

        progressBar.progress = mCurrentPosition
        progressBar.max = mQuestionsList!!.size
        tv_progress.text = "$mCurrentPosition" + "/" + progressBar.max

        tv_question.text = currentQuestion!!.question
        iv_image.setImageResource(currentQuestion.image)

        tv_option_one.text = currentQuestion.optionOne
        tv_option_two.text = currentQuestion.optionTwo
        tv_option_three.text = currentQuestion.optionThree
        tv_option_four.text = currentQuestion.optionFour
    }

    private fun defaultOptionsView(){
        val options = ArrayList<TextView>()
        options.add(0,tv_option_one)
        options.add(1,tv_option_two)
        options.add(2,tv_option_three)
        options.add(3,tv_option_four)

        for (option in options){
            option.setTextColor(Color.parseColor("#7A8089"))
            option.typeface = Typeface.DEFAULT
            option.background = ContextCompat.getDrawable(
                this,
                R.drawable.default_option_border_bg
            )
        }

    }

    private fun defaultSubmitView(){
        btn_submit.setTextColor(Color.parseColor("#E8E8E8"))
        btn_submit.typeface = Typeface.DEFAULT
        btn_submit.background = ContextCompat.getDrawable(this, R.drawable.default_submit_border_bg)
    }

    override fun onClick(p0: View?) {
        when(p0?.id){
            R.id.tv_option_one -> {
                selectedOptionView(tv_option_one, 1)
                submit()
            }
            R.id.tv_option_two -> {
                selectedOptionView(tv_option_two, 2)
                submit()
            }
            R.id.tv_option_three -> {
                selectedOptionView(tv_option_three, 3)
                submit()
            }
            R.id.tv_option_four -> {
                selectedOptionView(tv_option_four, 4)
                submit()
            }
            R.id.btn_submit -> {
                submit()
            }
        }
    }

    private fun answerView(answer: Int, drawableView: Int){
        when (answer){
            1 -> tv_option_one.background = ContextCompat.getDrawable(this, drawableView)
            2 -> tv_option_two.background = ContextCompat.getDrawable(this, drawableView)
            3 -> tv_option_three.background = ContextCompat.getDrawable(this, drawableView)
            4 -> tv_option_four.background = ContextCompat.getDrawable(this, drawableView)
        }
    }

    private fun selectedOptionView(tv: TextView, selectedOptionNum: Int){
        mSelectedOptionPosition = selectedOptionNum

        if (attempts < 1){
            defaultOptionsView()

            tv.setTextColor(Color.parseColor("#363A43"))
            tv.setTypeface(tv.typeface, Typeface.BOLD)
            tv.background = ContextCompat.getDrawable(
                this,
                R.drawable.selected_option_border_bg
            )
        }
    }

    var attempts: Int = 0
    private fun submit(){
        if (mSelectedOptionPosition == 0){
            if (attempts == 0){
                MediaPlayer.create(this@EuropaQuestionsActivity, R.raw.error).start()
            }else{
                mCurrentPosition += 1
                attempts = 0

                when{
                    mCurrentPosition <= mQuestionsList!!.size -> setQuestion()
                    else -> {
                        val intent = Intent(this, ResultActivity::class.java)
                        intent.putExtra(Constants.USER_NAME, mUserName)
                        intent.putExtra(Constants.CORRECT_ANSWERS, mCorrectAnswers)
                        intent.putExtra(Constants.TOTAL_QUESTIONS, mQuestionsList!!.size)
                        startActivity(intent)
                        finish()
                    }
                }
            }

        }else {
            val question = mQuestionsList?.get(mCurrentPosition - 1)
            val wrong_music = listOf<MediaPlayer>(
                MediaPlayer.create(this@EuropaQuestionsActivity, R.raw.thud),
                //MediaPlayer.create(this@EuropaQuestionsActivity, R.raw.bruh),
                //MediaPlayer.create(this@EuropaQuestionsActivity, R.raw.augh)
            )
            if (attempts < 1){
                if(question!!.correctAnswer != mSelectedOptionPosition){
                    answerView(mSelectedOptionPosition, R.drawable.wrong_option_border_bg)
                    wrong_music.shuffled().last().start()
                }else{
                    MediaPlayer.create(this@EuropaQuestionsActivity, R.raw.correct).start()
                    mCorrectAnswers += 1
                }
                answerView(question.correctAnswer, R.drawable.correct_option_border_bg)
            }

            btn_submit.background = ContextCompat.getDrawable(this, R.drawable.available_submit_bg)

            attempts += 1

            mSelectedOptionPosition = 0
        }

    }

    private fun getQuestions(): ArrayList<Question>{
        val questionsList = ArrayList<Question>()
        val questionText = "Hvilket flag er dette?"

        val flaggNavnList = europa
        val drawNameList = europa_draw
        var antall = 0
        when (intent.getStringExtra(Constants.DIFF)){
            "easy" -> antall = 10
            "medium" -> antall = 25
            "hard" -> antall = 47
        }

        var id = 1
        val brukteFlaggNavn = ArrayList<String>()
        for (i in (0 until flaggNavnList.size)){
            var gjeldeneFlaggIndex: Int = 0
            var gjeldeneFlaggNavn: String = ""

            var cond = true
            while (cond){
                val nyFlaggIndex = (0..flaggNavnList.size-1).shuffled().last()
                val nyFlaggNavn = flaggNavnList[nyFlaggIndex]
                if (nyFlaggNavn !in brukteFlaggNavn){
                    gjeldeneFlaggIndex = nyFlaggIndex
                    gjeldeneFlaggNavn = nyFlaggNavn
                    brukteFlaggNavn.add(gjeldeneFlaggNavn)
                    cond = false
                }
            }

            val variableValue: String = drawNameList[gjeldeneFlaggIndex]
            val bildeInt: Int = resources.getIdentifier(variableValue, "drawable", getPackageName())

            var alternativ = ArrayList<String>()
            while (alternativ.size < 4){
                val randomAltIndex = (0..flaggNavnList.size-1).shuffled().last()
                val randomAltFlaggNavn = flaggNavnList[randomAltIndex]
                if (randomAltFlaggNavn !in alternativ){
                    alternativ.add(randomAltFlaggNavn)
                }
            }

            var riktigSvarPos: Int = 0
            if (gjeldeneFlaggNavn in alternativ){
                riktigSvarPos = alternativ.indexOf(gjeldeneFlaggNavn)
            }else{
                riktigSvarPos = Random.nextInt(0, 4)
                alternativ[riktigSvarPos] = gjeldeneFlaggNavn
            }

            val que = Question(
                id,
                questionText,
                bildeInt,
                alternativ[0].capitalize(),
                alternativ[1].capitalize(),
                alternativ[2].capitalize(),
                alternativ[3].capitalize(),
                riktigSvarPos+1
            )

            if (questionsList.size < antall){
                questionsList.add(que)
            }else{
                break
            }

            id += 1
        }

        return questionsList
    }

}