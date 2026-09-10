// 1. قاعدة البيانات المحلية (Local Storage) والمتغيرات العامة
let transactions = JSON.parse(localStorage.getItem('erp_transactions')) || [];
let editingTransactionId = null; // متغير سري لتتبع رقم الحركة التي يتم تعديلها حالياً

// 2. دوال فتح وإغلاق النوافذ المنبثقة (Modals)
function openModal(modalId) {
    document.getElementById(modalId).classList.remove('hidden');
}

function closeModal(modalId) {
    document.getElementById(modalId).classList.add('hidden');
    
    // تصفير النماذج وإلغاء حالة التعديل عند الإغلاق
    if(modalId === 'stockInModal') document.getElementById('stockInForm').reset();
    if(modalId === 'stockOutModal') document.getElementById('stockOutForm').reset();
    editingTransactionId = null; 
}

// 3. دالة استدعاء البيانات للتعديل (زر القلم)
function editTransaction(id) {
    // البحث عن الحركة المطلوبة في قاعدة البيانات
    const tx = transactions.find(t => t.id === id);
    if (!tx) return;

    editingTransactionId = id; // إخبار النظام أننا في وضع "تعديل" وليس "إضافة"

    // إذا كانت الحركة "وارد"
    if (tx.type === 'وارد') {
        document.getElementById('inCutNumber').value = tx.cutNumber;
        document.getElementById('inModelName').value = tx.modelName;
        document.getElementById('inPieces').value = tx.pieces;
        document.getElementById('inDate').value = tx.date;
        // ... (يمكنك إضافة باقي الحقول هنا)
        
        openModal('stockInModal');
    } 
    // إذا كانت الحركة "صرف"
    else if (tx.type === 'صرف') {
        document.getElementById('outCutNumberSelect').value = tx.cutNumber;
        document.getElementById('outPieces').value = tx.pieces;
        document.getElementById('outParty').value = tx.party;
        document.getElementById('outDate').value = tx.date;
        // ... (يمكنك إضافة باقي الحقول هنا)
        
        openModal('stockOutModal');
    }
}

// 4. دالة حفظ الوارد (تتعامل مع الإضافة والتعديل معاً)
function handleStockInSubmit(e) {
    e.preventDefault(); // منع تحديث الصفحة
    
    const formData = {
        id: editingTransactionId ? editingTransactionId : Date.now().toString(), // الاحتفاظ بالـ ID القديم أو إنشاء جديد
        type: 'وارد',
        cutNumber: document.getElementById('inCutNumber').value,
        modelName: document.getElementById('inModelName').value,
        pieces: parseInt(document.getElementById('inPieces').value),
        date: document.getElementById('inDate').value
    };

    if (editingTransactionId) {
        // تحديث الحركة القديمة (Replace)
        const index = transactions.findIndex(t => t.id === editingTransactionId);
        transactions[index] = formData;
    } else {
        // إضافة حركة جديدة كلياً
        transactions.push(formData);
    }

    // حفظ البيانات وتحديث الواجهة
    localStorage.setItem('erp_transactions', JSON.stringify(transactions));
    closeModal('stockInModal');
    renderTransactionsTable(); 
    showToast("تم حفظ حركة الوارد بنجاح!");
}

// 5. دالة عرض البيانات في جدول الحركات ووضع زر التعديل
function renderTransactionsTable() {
    const tbody = document.getElementById('transactionsTableBody');
    if (!tbody) return;
    
    tbody.innerHTML = ''; // تفريغ الجدول قبل إعادة الملء
    
    transactions.forEach(tx => {
        const tr = document.createElement('tr');
        tr.innerHTML = `
            <td class="p-3">${tx.cutNumber}</td>
            <td class="p-3">${tx.modelName || '-'}</td>
            <td class="p-3 text-center">
                <span class="px-2 py-1 rounded text-xs font-bold ${tx.type === 'وارد' ? 'bg-green-100 text-green-800' : 'bg-red-100 text-red-800'}">
                    ${tx.type}
                </span>
            </td>
            <td class="p-3 text-center">${tx.pieces}</td>
            <td class="p-3 text-center">${tx.date}</td>
            <td class="p-3 text-center">
                <!-- زر التعديل يستدعي دالة التعديل ويمرر لها الـ ID -->
                <button onclick="editTransaction('${tx.id}')" class="text-blue-600 hover:text-blue-800 mx-1 cursor-pointer" title="تعديل">
                    <i class="fa-solid fa-pen"></i>
                </button>
                <button onclick="deleteTransaction('${tx.id}')" class="text-red-600 hover:text-red-800 mx-1 cursor-pointer" title="حذف">
                    <i class="fa-solid fa-trash"></i>
                </button>
            </td>
        `;
        tbody.appendChild(tr);
    });
}

// 6. دالة بسيطة للإشعارات (Toast)
function showToast(message) {
    const container = document.getElementById('toastContainer');
    const toast = document.createElement('div');
    toast.className = 'bg-excel-700 text-white px-4 py-2 rounded shadow-lg font-bold text-sm transition opacity-100';
    toast.innerText = message;
    container.appendChild(toast);
    
    setTimeout(() => {
        toast.style.opacity = '0';
        setTimeout(() => toast.remove(), 500);
    }, 3000);
}

// تشغيل العرض عند تحميل الصفحة
window.onload = () => {
    renderTransactionsTable();
};
