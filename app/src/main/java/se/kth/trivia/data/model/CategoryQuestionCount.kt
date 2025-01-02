package se.kth.trivia.data.model

data class CategoryQuestionCount(
    val category_id: Int,
    val category_question_count: QuestionCount,
)

data class QuestionCount(
    val total_question_count: Int,
    val total_easy_question_count: Int,
    val total_medium_question_count: Int,
    val total_hard_question_count: Int,
)
