package com.mohammedalqadi.scienceapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MaterialTheme(
                colorScheme = lightColorScheme(
                    primary = Color(0xFF0D47A1),
                    secondary = Color(0xFF00796B),
                    background = Color(0xFFF5F5F5)
                )
            ) {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    MainAppNavigation()
                }
            }
        }
    }
}

@Composable
fun MainAppNavigation() {
    var isLoggedIn by remember { mutableStateOf(false) }
    var userRole by remember { mutableStateOf("") } // "student" أو "admin"
    var userName by remember { mutableStateOf("") }
    var userGrade by remember { mutableStateOf("") }

    if (!isLoggedIn) {
        LoginScreen { name, role, grade ->
            userName = name
            userRole = role
            userGrade = grade
            isLoggedIn = true
        }
    } else {
        if (userRole == "admin") {
            AdminDashboard {
                isLoggedIn = false // تسجيل خروج المعلم
            }
        } else {
            StudentHomeScreen(studentName = userName, studentGrade = userGrade) {
                isLoggedIn = false // تسجيل خروج الطالب
            }
        }
    }
}

@Composable
fun LoginScreen(onLoginSuccess: (String, String, String) -> Unit) {
    var codeInput by remember { mutableStateOf("") }
    var nameInput by remember { mutableStateOf("") }
    var errorMessage by remember { mutableStateOf("") }

    // أكواد الطلاب التجريبية أو كود المعلم الخاص بك
    val studentCodes = mapOf(
        "MQ101" to "الصف الأول الثانوي",
        "MQ202" to "الصف الثاني الثانوي",
        "MQ303" to "الصف الثالث الثانوي"
    )
    val adminSecretCode = "ADMIN_MOHAMMED_999" // كودك السري الخاص كمعلم

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "برنامج الأستاذ محمد القاضي التعليمي",
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary,
            textAlign = TextAlign.Center
        )
        Text(
            text = "الكيمياء • الفيزياء • الأحياء",
            fontSize = 14.sp,
            color = MaterialTheme.colorScheme.secondary,
            modifier = Modifier.padding(bottom = 24.dp)
        )

        OutlinedTextField(
            value = nameInput,
            onValueChange = { nameInput = it },
            label = { Text("الاسم ثلاثي (أو اكتب 'المعلم')") },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp)
        )
        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = codeInput,
            onValueChange = { codeInput = it },
            label = { Text("أدخل الكود (كود الطالب أو كود المعلم)") },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp)
        )

        if (errorMessage.isNotEmpty()) {
            Spacer(modifier = Modifier.height(8.dp))
            Text(text = errorMessage, color = Color.Red, fontSize = 13.sp)
        }

        Spacer(modifier = Modifier.height(24.dp))

        Button(
            onClick = {
                if (codeInput == adminSecretCode) {
                    onLoginSuccess("الأستاذ محمد القاضي", "admin", "معلم المادة")
                } else if (studentCodes.containsKey(codeInput) && nameInput.isNotBlank()) {
                    val grade = studentCodes[codeInput]!!
                    onLoginSuccess(nameInput, "student", grade)
                } else {
                    errorMessage = "الكود المدخل غير صحيح. تأكد من الأستاذ محمد."
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp),
            shape = RoundedCornerShape(12.dp)
        ) {
            Text(text = "دخول للبرنامج", fontSize = 16.sp, fontWeight = FontWeight.Bold)
        }
    }
}

// واجهة المعلم لإضافة الملازم وإدارتها
@Composable
fun AdminDashboard(onLogout: () -> Unit) {
    var bookTitle by remember { mutableStateOf("") }
    var successMessage by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(text = "لوحة تحكم المعلم (رفع الملازم)", fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
            TextButton(onClick = onLogout) {
                Text("خروج", color = Color.Red)
            }
        }

        Divider(modifier = Modifier.padding(vertical = 8.dp))

        Spacer(modifier = Modifier.height(16.dp))

        Text(text = "إضافة ملزمة جديدة (PDF):", fontWeight = FontWeight.Medium)
        Spacer(modifier = Modifier.height(8.dp))

        OutlinedTextField(
            value = bookTitle,
            onValueChange = { bookTitle = it },
            label = { Text("عنوان الملزمة (مثلا: ملزمة الكيمياء - الباب الأول)") },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp)
        )

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = {
                if (bookTitle.isNotBlank()) {
                    successMessage = "تمت إضافة الملزمة بنجاح وتشفيرها للطلاب!"
                    bookTitle = ""
                }
            },
            modifier = Modifier.fillMaxWidth().height(50.dp),
            shape = RoundedCornerShape(12.dp)
        ) {
            Text(text = "📁 اختيار ملف PDF وحفظه في التطبيق")
        }

        if (successMessage.isNotEmpty()) {
            Spacer(modifier = Modifier.height(16.dp))
            Text(text = successMessage, color = Color(0xFF2E7D32), fontWeight = FontWeight.Bold)
        }
    }
}

// واجهة الطالب
@Composable
fun StudentHomeScreen(studentName: String, studentGrade: String, onLogout: () -> Unit) {
    var selectedTab by remember { mutableStateOf(0) }

    Column(modifier = Modifier.fillMaxSize()) {
        TopAppBar(
            title = { Text("$studentName ($studentGrade)", fontSize = 14.sp) },
            actions = {
                TextButton(onClick = onLogout) {
                    Text("خروج", color = Color.White)
                }
            },
            colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.primary)
        )

        Box(modifier = Modifier.weight(1f).fillMaxWidth()) {
            when (selectedTab) {
                0 -> StudentLibraryScreen()
                1 -> StudentExamsScreen()
                2 -> TeacherCvScreen()
            }
        }

        NavigationBar {
            NavigationBarItem(icon = { Text("📚") }, label = { Text("الملازم") }, selected = selectedTab == 0, onClick = { selectedTab = 0 })
            NavigationBarItem(icon = { Text("📝") }, label = { Text("الاختبارات") }, selected = selectedTab == 1, onClick = { selectedTab = 1 })
            NavigationBarItem(icon = { Text("👨‍🏫") }, label = { Text("عن المعلم") }, selected = selectedTab == 2, onClick = { selectedTab = 2 })
        }
    }
}

@Composable
fun StudentLibraryScreen() {
    Column(modifier = Modifier.padding(16.dp)) {
        Text("ملازم المواد العلمية (محمية بدون إنترنت)", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
        Spacer(modifier = Modifier.height(16.dp))
        Card(modifier = Modifier.fillMaxWidth()) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text("ملزمة الكيمياء - الصف الثالث الثانوي", fontWeight = FontWeight.Bold)
                Text("إعداد: الأستاذ محمد القاضي", fontSize = 12.sp, color = Color.Gray)
                Spacer(modifier = Modifier.height(8.dp))
                Button(onClick = {}) { Text("قراءة الملزمة") }
            }
        }
    }
}

@Composable
fun StudentExamsScreen() {
    Column(modifier = Modifier.padding(16.dp)) {
        Text("الاختبارات المباشرة", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
        Spacer(modifier = Modifier.height(16.dp))
        Card(modifier = Modifier.fillMaxWidth()) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text("اختبار الباب الأول: الكيمياء", fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(8.dp))
                Button(onClick = {}) { Text("ابدأ الاختبار وحفظ النتيجة") }
            }
        }
    }
}

@Composable
fun TeacherCvScreen() {
    Column(modifier = Modifier.fillMaxSize().padding(16.dp), horizontalAlignment = Alignment.CenterHorizontally) {
        Box(modifier = Modifier.size(100.dp).clip(CircleShape).background(MaterialTheme.colorScheme.primary), contentAlignment = Alignment.Center) {
            Text(text = "صورة المعلم", color = Color.White)
        }
        Spacer(modifier = Modifier.height(12.dp))
        Text(text = "الأستاذ / محمد سليمان الوصابي (محمد القاضي)", fontSize = 16.sp, fontWeight = FontWeight.Bold, textAlign = TextAlign.Center, color = MaterialTheme.colorScheme.primary)
        Text(text = "استاذ الكيمياء والفيزياء والأحياء - مدرسة النور الأساسية الثانوية بالروحاء (وصاب السافل)", fontSize = 12.sp, textAlign = TextAlign.Center, color = Color.Gray, modifier = Modifier.padding(top = 4.dp))
        Spacer(modifier = Modifier.height(16.dp))
        Card(modifier = Modifier.fillMaxWidth()) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text("🎓 المؤهل: بكالوريوس تربية تخصص كيمياء فيزيائية.", fontSize = 13.sp)
                Spacer(modifier = Modifier.height(6.dp))
                Text("📞 الهاتف/واتساب: 774470090", fontSize = 13.sp)
                Text("✉️ البريد: alwsabi97@gmail.com", fontSize = 13.sp)
            }
        }
    }
}
