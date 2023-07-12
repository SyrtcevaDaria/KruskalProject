package utils

import Edge

object strings {
    const val buttonManual = "Создать вручную"
    const val buttonFromFile = "Загрузить из файла"
    const val instructions = "2 нажатия на вершину = удаление\n" +
            "1 нажатие на ребро = удаление\n" +
            "При нажатии на вершину вы можете построить ребро"
    const val buttonAddVertex = "Добавить вершину"
    const val buttonNext = "Дальше"
    const val buttonPrevious = "Назад"
    const val buttonComplete = "Закончить"
    const val buttonStart = "Начать"
    fun stepDescription(edge: Edge) = "Ребро ${edge.src} - ${edge.dest} с минимальным весом ${edge.weight}"
    const val stepBottom = "Поэтому добавляем в каркас"
    fun edgeWeight(index: Int) = index.toString()
    fun vertexNumber(index: Int) = index.toString()
    const val enterNumber = "Введите число"
    const val buttonAccept = "Подтвердить"
}
