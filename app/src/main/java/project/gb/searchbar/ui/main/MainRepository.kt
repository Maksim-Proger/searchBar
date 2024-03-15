package project.gb.searchbar.ui.main

class MainRepository {
    suspend fun getData(str: String) : String {
        return "По вашему запросу '$str' не удалось ничего найти!"
    }
}