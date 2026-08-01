package com.example.data.repository

import android.content.Context
import com.example.data.api.GeminiStoryClient
import com.example.data.api.StoryAiResponse
import com.example.data.db.*
import com.example.data.model.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import org.json.JSONArray
import org.json.JSONObject

class StoryRepository(
    private val db: AppDatabase,
    private val apiClient: GeminiStoryClient = GeminiStoryClient()
) {
    private val storyDao = db.storyDao()
    private val chatDao = db.chatMessageDao()
    private val charDao = db.characterStateDao()
    private val achievementDao = db.achievementDao()
    private val socialDao = db.socialPostDao()

    val allStories: Flow<List<World>> = storyDao.getAllStories().map { entities ->
        entities.map { it.toDomainWorld() }
    }

    val achievements: Flow<List<AchievementEntity>> = achievementDao.getAllAchievements()
    val socialPosts: Flow<List<SocialPostEntity>> = socialDao.getAllPosts()

    suspend fun initializeDefaultDataIfEmpty() {
        val existingStories = storyDao.getAllStories().first()
        if (existingStories.size < 25) {
            val defaultStories = listOf(
                // --- DARK FANTASY & MAGIC ---
                StoryEntity(
                    id = "oasis_1",
                    title = "Oasis Above the Ashes",
                    genreName = WorldGenre.DARK_FANTASY.name,
                    description = "Золотой шпиль древней башни возвышается над облаками и пустынным оазисом. Магия предков хранит тайны павшей цивилизации.",
                    loreSummary = "Вы — хранитель оазиса. Владыка башни призывает вас для расследования древнего ритуала.",
                    specialMechanicName = "Магический Оазис",
                    primaryCharacterName = "Магистр Аурус",
                    primaryCharacterRole = "Страж Оазиса",
                    initialMessage = "— Взгляни на облака, Искатель. Башня Света снова подает сигнал!",
                    initialChoicesJson = "Войти в башню,Осмотреть оазис,Призвать ветры",
                    defaultImagePrompt = "Golden fantasy spire tower above clouds and desert oasis at sunset",
                    isCustom = false
                ),
                StoryEntity(
                    id = "world_echo",
                    title = "World Echo",
                    genreName = WorldGenre.DARK_FANTASY.name,
                    description = "Уютная средневековая таверна и старинное поместье, где каждый шепот эхом отзывается в параллельных мирах.",
                    loreSummary = "Древняя таверна на перекрестке измерений.",
                    specialMechanicName = "Резонанс Эха",
                    primaryCharacterName = "Элара Тавернщица",
                    primaryCharacterRole = "Хранительница Эха",
                    initialMessage = "— Добро пожаловать, путник. Какую историю ты принес с собой?",
                    initialChoicesJson = "Спросить о слухах,Заказать эль,Достать карту миров",
                    defaultImagePrompt = "Medieval fantasy cozy tavern with glowing warm windows",
                    isCustom = false
                ),
                StoryEntity(
                    id = "sovereign_1",
                    title = "Sovereign of Flames",
                    genreName = WorldGenre.DARK_FANTASY.name,
                    description = "Красная планета, огненные пики и алый гигантский диск луны. Владыка Огня пробуждается от векового сна.",
                    loreSummary = "Вулканический мир под властью Драконьего Владыки.",
                    specialMechanicName = "Ярость Пламени",
                    primaryCharacterName = "Игнис Владыка",
                    primaryCharacterRole = "Повелитель Огня",
                    initialMessage = "— Земля под ногами дрожит. Пламя требует жертвы!",
                    initialChoicesJson = "Оседлать дракона,Сковать огненный меч,Принять пламя",
                    defaultImagePrompt = "Fiery red landscape with jagged peaks and giant blood moon",
                    isCustom = false
                ),
                StoryEntity(
                    id = "academy_magic",
                    title = "Академия Высокого Чародейства",
                    genreName = WorldGenre.DARK_FANTASY.name,
                    description = "Тайный университет магии в заоблачной цитадели. Древние гримуары и дуэли стихий.",
                    loreSummary = "Академия Арканум обучает избранных обладателей перворожденного дара.",
                    specialMechanicName = "Магический Резонанс",
                    primaryCharacterName = "Архимаг Валериус",
                    primaryCharacterRole = "Ректор Ордена",
                    initialMessage = "— Твой мантия излучает сияние, студент. Готов к вступительным испытаниям?",
                    initialChoicesJson = "Открыть Гримуар Огня,Зайти в Запретную Библиотеку,Вызвать на дуэль",
                    defaultImagePrompt = "Grand magic academy citadel with floating magical orbs",
                    isCustom = false
                ),
                StoryEntity(
                    id = "dragon_lair",
                    title = "Логово Теневого Дракона",
                    genreName = WorldGenre.DARK_FANTASY.name,
                    description = "Забытый подземный лабиринт, полный сокровищ и древнего проклятия теневого змея.",
                    loreSummary = "Пещеры Крога хранят рубин вечности.",
                    specialMechanicName = "Защита от Тень-Огня",
                    primaryCharacterName = "Никс Провожатый",
                    primaryCharacterRole = "Партизан-Вор",
                    initialMessage = "— Тихо! Если Дракон проснется, даже пепел от нас не останется...",
                    initialChoicesJson = "Обезвредить ловушку,Проскользнуть вдоль стены,Подготовить заклинание",
                    defaultImagePrompt = "Dark cavern with glowing dragon hoard gold and sleeping dragon silhouette",
                    isCustom = false
                ),
                StoryEntity(
                    id = "elven_citadel",
                    title = "Цитадель Лунных Эльфов",
                    genreName = WorldGenre.DARK_FANTASY.name,
                    description = "Серебряный лес и сияющие эльфийские шпили, охраняющие древо жизни от скверны.",
                    loreSummary = "Лес Итилиэн находится под осадой порождений тьмы.",
                    specialMechanicName = "Благословение Луны",
                    primaryCharacterName = "Следопыт Лириэль",
                    primaryCharacterRole = "Капитан Стражи",
                    initialMessage = "— Наш лес плачет, чужеземец. Темные твари подобрались к Корню Древа.",
                    initialChoicesJson = "Натянуть лук,Окропить стрелы святой водой,Провести ритуал",
                    defaultImagePrompt = "Mystical elven silver citadel in bioluminescent forest",
                    isCustom = false
                ),
                StoryEntity(
                    id = "necromancer_curse",
                    title = "Проклятие Проклятого Короля",
                    genreName = WorldGenre.DARK_FANTASY.name,
                    description = "Заброшенное некротическое королевство, где мертвые правят живыми.",
                    loreSummary = "Король-Лич поднял армию костей для последнего похода.",
                    specialMechanicName = "Аура Некромантии",
                    primaryCharacterName = "Паладин Галахад",
                    primaryCharacterRole = "Рыцарь Святого Света",
                    initialMessage = "— Мой щит еще держит свет, но полчища мертвецов заполнили всю долину!",
                    initialChoicesJson = "Воздеть святой крест,Обнажить благословенный клинок,Отступить к цитадели",
                    defaultImagePrompt = "Gothic dark castle surrounded by glowing blue undead army",
                    isCustom = false
                ),
                StoryEntity(
                    id = "sunken_atlantis",
                    title = "Подводная Атлантида",
                    genreName = WorldGenre.DARK_FANTASY.name,
                    description = "Затонувший купольный город древних богов на океанском дне.",
                    loreSummary = "Глубинный народ охраняет кристаллы управления трезубцем.",
                    specialMechanicName = "Давление Бездны",
                    primaryCharacterName = "Сирена Калипсо",
                    primaryCharacterRole = "Страж Кристалла",
                    initialMessage = "— Ты дышишь под водой благодаря нашей магии, но океан не прощает ошибок.",
                    initialChoicesJson = "Исследовать подводный храм,Активировать гидро-кристалл,Заговорить с Калипсо",
                    defaultImagePrompt = "Underwater glowing sunken city with coral architecture and bioluminescence",
                    isCustom = false
                ),

                // --- CYBERPUNK & NEON FUTURISM ---
                StoryEntity(
                    id = "cyber_1",
                    title = "Киберпанк 2088: Неоновый Горизонт",
                    genreName = WorldGenre.CYBERPUNK.name,
                    description = "Верхние уровни мегаполиса утопают в неоновом свете реклам, а нижние кишат кибер-бандами и хакерами.",
                    loreSummary = "Корпорация 'Арасака-Прайм' запустила протокол тотального контроля разума.",
                    specialMechanicName = "Нейро-Взлом",
                    primaryCharacterName = "Валкирия",
                    primaryCharacterRole = "Сетевой Взломщик",
                    initialMessage = "— Подключайся к деке, напарник! Корпоранты уже на хвосте...",
                    initialChoicesJson = "Взломать терминал,Обнажить моноструну,Уйти по крышам",
                    defaultImagePrompt = "Futuristic neon cyberpunk city with flying cars at rainy night",
                    isCustom = false
                ),
                StoryEntity(
                    id = "bprd_1",
                    title = "B.P.R.D Field Agent",
                    genreName = WorldGenre.DETECTIVE.name,
                    description = "Мрачный готический собор под лунным светом. Спецагенты бюро расследуют аномальные явления.",
                    loreSummary = "Заснеженный замок скрывает культ теней.",
                    specialMechanicName = "Аномальный Детектор",
                    primaryCharacterName = "Агент Хеллбой",
                    primaryCharacterRole = "Старший Агент Бюро",
                    initialMessage = "— Снег смывает следы, но демонический резонанс никуда не делся...",
                    initialChoicesJson = "Зарядить револьвер,Активировать детектор,Войти в собор",
                    defaultImagePrompt = "Gothic dark snowy cathedral under full moon night with glowing archway",
                    isCustom = false
                ),
                StoryEntity(
                    id = "netrunner_protocol",
                    title = "Протокол 'Сеть-Zero'",
                    genreName = WorldGenre.CYBERPUNK.name,
                    description = "Виртуальное пространство кибервселенной, где программы обрели сознание.",
                    loreSummary = "Глобальный ИИ 'Оракул' заблокировал главный файрвол мегасети.",
                    specialMechanicName = "Оверклокинг Мозга",
                    primaryCharacterName = "Хакер ЗЕРО",
                    primaryCharacterRole = "Легенда Киберпространства",
                    initialMessage = "— Твой аватар сбоит! Сканеры файрвола уже фиксируют наше присутствие.",
                    initialChoicesJson = "Запустить вирус-взломщик,Зашифровать поток,Перегрузить ядра",
                    defaultImagePrompt = "Abstract cyberspace matrix with neon glowing nodes and grid",
                    isCustom = false
                ),
                StoryEntity(
                    id = "chromed_samurai",
                    title = "Хромированный Самурай",
                    genreName = WorldGenre.CYBERPUNK.name,
                    description = "Уличные войны синдикатов Нео-Токио. Сталь, кибернетические импланты и честь.",
                    loreSummary = "Клан Красного Дракона ведет войну за контроль над фабриками имплантов.",
                    specialMechanicName = "Режим Катана-Сверхскорость",
                    primaryCharacterName = "Рю Кенсей",
                    primaryCharacterRole = "Кибер-Самурай",
                    initialMessage = "— Моя левая рука из титана, а глаза видят траектории пуль. Вперед!",
                    initialChoicesJson = "Достать термо-катану,Бросить светошумовую,Взломать оптику врага",
                    defaultImagePrompt = "Cyberpunk samurai with glowing katana in rainy neon alleyway",
                    isCustom = false
                ),
                StoryEntity(
                    id = "corporate_heist",
                    title = "Ограбление Корпорации 'Aegis'",
                    genreName = WorldGenre.CYBERPUNK.name,
                    description = "Проникновение в 100-этажный небоскреб корпоративного гиганта ради прототипа ИИ.",
                    loreSummary = "Секретный чип содержит код бессмертия.",
                    specialMechanicName = "Маскировочный Стелс",
                    primaryCharacterName = "Мира Мех",
                    primaryCharacterRole = "Специалист по Оборудованию",
                    initialMessage = "— Дроны отключены на 30 секунд. У тебя есть ровно один шанс спуститься в шахту лифта.",
                    initialChoicesJson = "Спрыгнуть на трос,Отключить сенсоры движения,Подкупить охрану",
                    defaultImagePrompt = "High-tech corporate skyscraper vault with laser security grid",
                    isCustom = false
                ),

                // --- SCI-FI & SPACE ODYSSEY ---
                StoryEntity(
                    id = "raven_1",
                    title = "Raven Beyond the Wormhole",
                    genreName = WorldGenre.SCI_FI.name,
                    description = "Космическая станция у границы червоточины. Потусторонние сигналы и передовые технологии будущего.",
                    loreSummary = "Корабль 'Ворон' прошел сквозь червоточину в параллельную галактику.",
                    specialMechanicName = "Квантовый Сдвиг",
                    primaryCharacterName = "ИИ 'Ворон'",
                    primaryCharacterRole = "Бортовой Квантовый Разум",
                    initialMessage = "— Сканеры зафиксировали искажение пространства! Мы внутри червоточины.",
                    initialChoicesJson = "Стабилизировать реактор,Выйти в открытый космос,Сделать квантовый скачок",
                    defaultImagePrompt = "Sci-fi astronaut in glowing control room facing golden wormhole portal",
                    isCustom = false
                ),
                StoryEntity(
                    id = "odyssey_1",
                    title = "The Odyssey",
                    genreName = WorldGenre.SCI_FI.name,
                    description = "Штормовое море, гигантский маяк-крепость и легендарный парусник, преодолевающий бушующие волны судьбы.",
                    loreSummary = "Морская и космическая одиссея сквозь века.",
                    specialMechanicName = "Штурвал Судьбы",
                    primaryCharacterName = "Капитан Немо",
                    primaryCharacterRole = "Легендарный Мореплаватель",
                    initialMessage = "— Полный вперед! Маяк указывает путь сквозь бурю!",
                    initialChoicesJson = "Держать курс на маяк,Опустить паруса,Приготовиться к бою",
                    defaultImagePrompt = "Majestic fantasy sailing ship in stormy golden sea next to giant lighthouse",
                    isCustom = false
                ),
                StoryEntity(
                    id = "mars_colony",
                    title = "Колония Марс-1: Мятеж",
                    genreName = WorldGenre.SCI_FI.name,
                    description = "Красная пыль, купольные города и борьба за кислород между поселенцами и Земной Федерацией.",
                    loreSummary = "Шахтеры гелия-3 объявили независимость марсианских куполов.",
                    specialMechanicName = "Баланс Кислорода",
                    primaryCharacterName = "Инженер Марк",
                    primaryCharacterRole = "Лидер Шахтеров",
                    initialMessage = "— Купол №4 заблокирован! Нам нужно восстановить подачу воздуха до заката.",
                    initialChoicesJson = "Перезапустить генератор,Захватить диспетчерскую,Вступить в переговоры",
                    defaultImagePrompt = "Red Mars dome colony landscape with glowing habitat structures",
                    isCustom = false
                ),
                StoryEntity(
                    id = "alien_contact",
                    title = "Первый Контакт: Сигнал Проксима",
                    genreName = WorldGenre.SCI_FI.name,
                    description = "Глубокий космос, встретивший исследовательский крейсер инопланетным монолитом неизвестного происхождения.",
                    loreSummary = "Монолит трансформирует материю вокруг себя.",
                    specialMechanicName = "Лингвистический Декодер",
                    primaryCharacterName = "Д-р Астра",
                    primaryCharacterRole = "Главный Ксенобиолог",
                    initialMessage = "— Сигнал не случайный! Это музыкальные частоты... Монолит отвечает на наши запросы!",
                    initialChoicesJson = "Отправить световой код,Прикоснуться к монолиту,Сканировать радиацию",
                    defaultImagePrompt = "Massive alien monolith floating in deep space near glowing nebula",
                    isCustom = false
                ),
                StoryEntity(
                    id = "time_paradox",
                    title = "Парадокс Времени: 3024",
                    genreName = WorldGenre.SCI_FI.name,
                    description = "Путешествия по альтернативным временным веткам, где одно решение меняет историю галактики.",
                    loreSummary = "Временная хроно-петля заперла станцию в бесконечном часе.",
                    specialMechanicName = "Хроно-Откат",
                    primaryCharacterName = "Хрономант Кронос",
                    primaryCharacterRole = "Страж Времени",
                    initialMessage = "— Мы переживаем этот час уже в сотый раз! Вспомни, что произошло перед взрывом!",
                    initialChoicesJson = "Изменить прошлый выбор,Остановить хроно-двигатель,Найти причину петли",
                    defaultImagePrompt = "Temporal time portal bending space with clocks and neon particles",
                    isCustom = false
                ),

                // --- DETECTIVE & NOIR ---
                StoryEntity(
                    id = "deadend_1",
                    title = "Dead End County",
                    genreName = WorldGenre.DETECTIVE.name,
                    description = "Забытый городок, туманные улицы и мистический нуар. За вами охотится вся полиция, но у вас своя цель.",
                    loreSummary = "Нуар детектив с неожиданной точки зрения.",
                    specialMechanicName = "Уровень Маскировки",
                    primaryCharacterName = "Призрак Гудзона",
                    primaryCharacterRole = "Информатор",
                    initialMessage = "— Сирены приближаются. Переулок заблокирован...",
                    initialChoicesJson = "Затеряться в тумане,Спрятаться в баре,Устроить диверсию",
                    defaultImagePrompt = "Dark hooded mysterious figures on eerie village street under full moon night",
                    isCustom = false
                ),
                StoryEntity(
                    id = "murder_express",
                    title = "Убийство в 'Восточном Экспрессе-3000'",
                    genreName = WorldGenre.DETECTIVE.name,
                    description = "Ретро-футуристичный поезд, несущийся сквозь заснеженные альпийские туннели. Опасный заговор в VIP-вагоне.",
                    loreSummary = "Загадочная смерть миллионера в закрытом купе.",
                    specialMechanicName = "Дедуктивный Анализ",
                    primaryCharacterName = "Детектив Холмс",
                    primaryCharacterRole = "Частный Сыщик",
                    initialMessage = "— Двери купе были заперты изнутри. Никаких следов взлома, но у жертвы пропал ключ...",
                    initialChoicesJson = "Осмотреть улики через лупу,Допросить дворецкого,Проверить алиби певицы",
                    defaultImagePrompt = "Vintage noir train interior with dim warm amber lights",
                    isCustom = false
                ),
                StoryEntity(
                    id = "foggy_london",
                    title = "Туманный Лондон: Тени Уайтчепела",
                    genreName = WorldGenre.DETECTIVE.name,
                    description = "Викторианский Лондон 1888 года, газовые фонари, булыжная мостовая и таинственные происшествия.",
                    loreSummary = "Орден Черного Ворона орудует в ночных переулках.",
                    specialMechanicName = "Газовый Инспекционный Фонарь",
                    primaryCharacterName = "Инспектор Лестрейд",
                    primaryCharacterRole = "Скотленд-Ярд",
                    initialMessage = "— Туман сегодня такой густой, что не видно собственного сапога. Осторожно!",
                    initialChoicesJson = "Зажечь фонарь,Изучить отпечатки на грязи,Опросить ночного сторожа",
                    defaultImagePrompt = "Victorian London foggy street with gas lamps and cobble stone",
                    isCustom = false
                ),

                // --- POST-APOCALYPSE & SURVIVAL ---
                StoryEntity(
                    id = "wasteland_survival",
                    title = "Пустошь 2099: Хранители Метро",
                    genreName = WorldGenre.POST_APOCALYPSE.name,
                    description = "Радиационная пустыня, заброшенные подземные бункеры и самодельные броневики.",
                    loreSummary = "После Великого Падения человечество выживает в сети укрепленных станций.",
                    specialMechanicName = "Счетчик Гейгера",
                    primaryCharacterName = "Ступор",
                    primaryCharacterRole = "Механик Пустоши",
                    initialMessage = "— Радиационный фон растет! Доставай фильтры для противогаза!",
                    initialChoicesJson = "Заменить фильтр,Завести броневик,Зарядить дробовик",
                    defaultImagePrompt = "Post-apocalyptic desolate wasteland with ruined highway and armored vehicle",
                    isCustom = false
                ),
                StoryEntity(
                    id = "biohazard_zone",
                    title = "Зона Заражения: Завод 404",
                    genreName = WorldGenre.POST_APOCALYPSE.name,
                    description = "Заброшенный био-комплекс, где аномальная флора поглотила бетон и сталь.",
                    loreSummary = "Мутантный вирус 'Флора-X' превращает растения в хищных монстров.",
                    specialMechanicName = "Антидот-Инжектор",
                    primaryCharacterName = "Егерь Гром",
                    primaryCharacterRole = "Сталкер Зоны",
                    initialMessage = "— Не дыши спорами! Эти красные цветы кусаются больно.",
                    initialChoicesJson = "Зажечь факел,Прорваться сквозь заросли,Применить антидот",
                    defaultImagePrompt = "Overgrown overgrown abandoned factory with glowing mutated bioluminescent vines",
                    isCustom = false
                ),
                StoryEntity(
                    id = "mech_scrappers",
                    title = "Охотники за Мехами: Ржавый Океан",
                    genreName = WorldGenre.POST_APOCALYPSE.name,
                    description = "Гигантские кладбища шагающих роботов в высохшем море. Поиск редких деталей.",
                    loreSummary = "Песчаные бури открывают павшие титаны древних войн.",
                    specialMechanicName = "Сканер Титанов",
                    primaryCharacterName = "Кайра Мусорщица",
                    primaryCharacterRole = "Пилот Скаута",
                    initialMessage = "— Взгляни! На вершине холма лежит титан класса 'Атлант'. Ядро еще фонит!",
                    initialChoicesJson = "Залезть в кабину титана,Извлечь плазменный реактор,Защитить находку",
                    defaultImagePrompt = "Giant rusted fallen mech robot in desert sand under stormy sky",
                    isCustom = false
                )
            )
            storyDao.insertStories(defaultStories)
        }

        val existingAch = achievementDao.getAllAchievements().first()
        if (existingAch.isEmpty()) {
            val defaultAchievements = listOf(
                AchievementEntity("ach_first_choice", "Первый Шаг", "Сделайте ваш первый выбор в истории", "touch_app", 50, true),
                AchievementEntity("ach_hacker", "Неоновый Повелитель", "Успешно взломайте терминал в Киберпанке", "memory", 100, false),
                AchievementEntity("ach_author", "Творец Миров", "Создайте собственный сценарий в редакторе", "edit_note", 150, false),
                AchievementEntity("ach_offline", "Автономный Странник", "Проведите историю в оффлайн режиме Gemma", "cloud_off", 100, false),
                AchievementEntity("ach_social", "Глас Народа", "Поделитесь вашей историей в социальной ленте", "share", 120, false)
            )
            achievementDao.insertAchievements(defaultAchievements)
        }

        val existingPosts = socialDao.getAllPosts().first()
        if (existingPosts.isEmpty()) {
            val defaultPosts = listOf(
                SocialPostEntity("p1", "Макс Роланд", "ic_avatar_1", "Oasis Above the Ashes", "Ребята, финал с Магистром Аурусом просто огонь! Набрал 210 Кредитов. Рекомендую всем!", 24, 6, "Выбор: Башня Света"),
                SocialPostEntity("p2", "Лирия Света", "ic_avatar_2", "World Echo", "Открыла секретную таверну в эхо-мире! Кеэлен выжил и мы создали свой гильдейский оазис.", 42, 11, "Выбор: Руна Эха"),
                SocialPostEntity("p3", "CyberNinja99", "ic_avatar_3", "Raven Beyond the Wormhole", "Создал свой сценарий про квантовый сдвиг в редакторе! Оцените сюжет и выбор решений.", 18, 3, "Создано в Редакторе Сценариев")
            )
            socialDao.insertPosts(defaultPosts)
        }
    }

    fun getMessagesForStory(storyId: String): Flow<List<StoryMessage>> {
        return chatDao.getMessagesForStory(storyId).map { entities ->
            entities.map { it.toDomainMessage() }
        }
    }

    fun getCharacterState(storyId: String): Flow<RpgStats> {
        return charDao.getCharacterState(storyId).map { entity ->
            entity?.toDomainRpgStats() ?: RpgStats()
        }
    }

    suspend fun sendUserChoice(
        storyId: String,
        userMessage: String,
        world: World,
        isOfflineMode: Boolean
    ): StoryMessage {
        val currentMessages = chatDao.getMessagesForStory(storyId).first()
        val historyPairs = currentMessages.map { it.sender to it.text }

        // Save user message
        val userMsgEntity = ChatMessageEntity(
            storyId = storyId,
            sender = "USER",
            senderName = "Игрок",
            text = userMessage,
            choicesJson = ""
        )
        chatDao.insertMessage(userMsgEntity)

        // Get current RPG state
        val currentStateEntity = charDao.getCharacterStateDirect(storyId)
        val currentStats = currentStateEntity?.toDomainRpgStats() ?: RpgStats()

        // Call Gemini / Gemma AI Client
        val aiResponse = apiClient.generateNextTurn(
            worldTitle = world.title,
            worldGenre = world.genre.titleRu,
            loreSummary = world.loreSummary,
            characterName = world.primaryCharacterName,
            rpgStats = currentStats,
            userMessage = userMessage,
            chatHistory = historyPairs,
            isOfflineMode = isOfflineMode
        )

        // Update stats with gained XP
        val newXp = currentStats.xp + aiResponse.xpGained
        val newLevel = if (newXp >= currentStats.level * 100) currentStats.level + 1 else currentStats.level
        val newHp = (currentStats.hp + aiResponse.hpDelta).coerceIn(1, currentStats.maxHp)

        val updatedStateEntity = CharacterStateEntity(
            storyId = storyId,
            playerLevel = newLevel,
            playerXp = newXp,
            hp = newHp,
            maxHp = currentStats.maxHp,
            energy = currentStats.manaOrEnergy,
            maxEnergy = currentStats.maxManaOrEnergy,
            intelligence = currentStats.intelligence,
            charisma = currentStats.charisma,
            combat = currentStats.combat,
            luck = currentStats.luck,
            credsOrGold = currentStats.credsOrGold + 20
        )
        charDao.saveCharacterState(updatedStateEntity)

        // Convert AI choices to JSON
        val choicesJsonArray = JSONArray()
        for (c in aiResponse.choices) {
            val obj = JSONObject()
            obj.put("id", c.id)
            obj.put("text", c.text)
            c.statCheck?.let { obj.put("statCheck", it) }
            obj.put("riskLevel", c.riskLevel)
            choicesJsonArray.put(obj)
        }

        val aiMsgEntity = ChatMessageEntity(
            storyId = storyId,
            sender = "CHARACTER",
            senderName = world.primaryCharacterName,
            text = aiResponse.storyText,
            choicesJson = choicesJsonArray.toString(),
            imageUrl = aiResponse.imagePrompt,
            statChanges = aiResponse.statChanges
        )
        chatDao.insertMessage(aiMsgEntity)

        return aiMsgEntity.toDomainMessage()
    }

    suspend fun resetAndStartStory(world: World): StoryMessage {
        chatDao.clearMessagesForStory(world.id)

        // Initial character state
        val initialState = CharacterStateEntity(storyId = world.id)
        charDao.saveCharacterState(initialState)

        val choicesJsonArray = JSONArray()
        for (choiceText in world.initialChoices) {
            val obj = JSONObject()
            obj.put("id", "init_${choiceText.hashCode()}")
            obj.put("text", choiceText)
            obj.put("riskLevel", "Обычный")
            choicesJsonArray.put(obj)
        }

        val initialMsg = ChatMessageEntity(
            storyId = world.id,
            sender = "CHARACTER",
            senderName = world.primaryCharacterName,
            text = world.initialMessage,
            choicesJson = choicesJsonArray.toString(),
            imageUrl = world.defaultImagePrompt,
            statChanges = "История началась! +100 XP"
        )
        chatDao.insertMessage(initialMsg)
        return initialMsg.toDomainMessage()
    }

    suspend fun createCustomWorld(
        title: String,
        genre: WorldGenre,
        description: String,
        lore: String,
        characterName: String,
        role: String,
        initialMsg: String,
        choices: List<String>,
        imagePrompt: String
    ): World {
        val customId = "custom_${System.currentTimeMillis()}"
        val entity = StoryEntity(
            id = customId,
            title = title,
            genreName = genre.name,
            description = description,
            loreSummary = lore,
            specialMechanicName = "Пользовательская Механика",
            primaryCharacterName = characterName,
            primaryCharacterRole = role,
            initialMessage = initialMsg,
            initialChoicesJson = choices.joinToString(","),
            defaultImagePrompt = imagePrompt.ifEmpty { "Cinematic artwork for $title" },
            isCustom = true,
            authorName = "Вы (Автор)"
        )
        storyDao.insertStory(entity)
        achievementDao.unlockAchievement("ach_author")
        return entity.toDomainWorld()
    }

    suspend fun publishPost(storyTitle: String, postText: String, choiceHighlight: String) {
        val newPost = SocialPostEntity(
            id = "post_${System.currentTimeMillis()}",
            authorName = "Вы (Искатель)",
            authorAvatar = "ic_avatar_user",
            storyTitle = storyTitle,
            postContent = postText,
            likesCount = 1,
            commentsCount = 0,
            choiceHighlight = choiceHighlight,
            isLikedByMe = true
        )
        socialDao.insertPosts(listOf(newPost))
        achievementDao.unlockAchievement("ach_social")
    }

    suspend fun likePost(id: String) {
        socialDao.likePost(id)
    }

    // Helper mappings
    private fun StoryEntity.toDomainWorld(): World {
        val genreEnum = try { WorldGenre.valueOf(genreName) } catch (e: Exception) { WorldGenre.CYBERPUNK }
        val choices = initialChoicesJson.split(",").map { it.trim() }.filter { it.isNotEmpty() }
        return World(
            id = id,
            title = title,
            genre = genreEnum,
            description = description,
            loreSummary = loreSummary,
            bgGradientHex = listOf("#1A0D36", "#0F0921"),
            specialMechanicName = specialMechanicName,
            primaryCharacterName = primaryCharacterName,
            primaryCharacterRole = primaryCharacterRole,
            initialMessage = initialMessage,
            initialChoices = if (choices.isEmpty()) listOf("Начать путешествие", "Осмотреться") else choices,
            defaultImagePrompt = defaultImagePrompt,
            isCustom = isCustom,
            authorName = authorName
        )
    }

    private fun ChatMessageEntity.toDomainMessage(): StoryMessage {
        val choiceList = mutableListOf<StoryChoice>()
        if (choicesJson.isNotEmpty()) {
            try {
                val array = JSONArray(choicesJson)
                for (i in 0 until array.length()) {
                    val obj = array.getJSONObject(i)
                    choiceList.add(
                        StoryChoice(
                            id = obj.optString("id", "c_$i"),
                            text = obj.optString("text", "Выбор $i"),
                            statCheck = if (obj.has("statCheck") && !obj.isNull("statCheck")) obj.getString("statCheck") else null,
                            riskLevel = obj.optString("riskLevel", "Обычный")
                        )
                    )
                }
            } catch (e: Exception) {
                // Split by comma fallback
                choicesJson.split(",").forEachIndexed { index, text ->
                    if (text.isNotBlank()) {
                        choiceList.add(StoryChoice("c_$index", text.trim()))
                    }
                }
            }
        }
        return StoryMessage(
            id = id,
            storyId = storyId,
            sender = sender,
            senderName = senderName,
            text = text,
            choices = choiceList,
            imageUrl = imageUrl,
            timestamp = timestamp,
            statChanges = statChanges
        )
    }

    private fun CharacterStateEntity.toDomainRpgStats(): RpgStats {
        return RpgStats(
            level = playerLevel,
            xp = playerXp,
            hp = hp,
            maxHp = maxHp,
            manaOrEnergy = energy,
            maxManaOrEnergy = maxEnergy,
            intelligence = intelligence,
            charisma = charisma,
            combat = combat,
            luck = luck,
            credsOrGold = credsOrGold,
            inventory = inventoryJson.split(",").map { it.trim() }.filter { it.isNotEmpty() }
        )
    }
}
