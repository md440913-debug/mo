/**
 * EXCEL CUTS WAREHOUSE ERP - WEB APP JAVASCRIPT
 * Full-featured responsive web implementation
 */

// ==========================================
// 1. DATA MODELS & STATE MANAGEMENT
// ==========================================

const STORAGE_KEY = 'EXCEL_CUTS_ERP_DATA_V2';

// Default initial dataset (preserves warehouse state)
const DEFAULT_DATA = {
  cuts: [
    {
      cutNumber: "CUT-101",
      modelName: "تيشرت أوفر سايز صيفي",
      fabricType: "قطن سنجل ليكرا",
      color: "أسود ملكي",
      season: "صيف 2025",
      targetPieces: 1200,
      notes: "قصة ممتازة لخامات التصدير",
      createdAt: "2025-05-10"
    },
    {
      cutNumber: "CUT-102",
      modelName: "قميص بولو كاجوال",
      fabricType: "بيكيه تركي",
      color: "أبيض ناصع",
      season: "صيف 2025",
      targetPieces: 850,
      notes: "تشغيل ورش الشروق",
      createdAt: "2025-05-12"
    },
    {
      cutNumber: "CUT-103",
      modelName: "بنطلون كارجو 6 جيوب",
      fabricType: "جابردين غاطس",
      color: "زيتي غامق",
      season: "صيف 2025",
      targetPieces: 1500,
      notes: "يتطلب تطريز للجيب الجانبي",
      createdAt: "2025-05-15"
    },
    {
      cutNumber: "CUT-104",
      modelName: "هودي شتوي مطبوع",
      fabricType: "ميلتون مبطن",
      color: "رمادي ميلانج",
      season: "شتاء 2025",
      targetPieces: 900,
      notes: "مراحل طباعة سلك سكرين",
      createdAt: "2025-05-18"
    }
  ],
  transactions: [
    {
      id: "TX-01",
      cutNumber: "CUT-101",
      modelName: "تيشرت أوفر سايز صيفي",
      type: "وارد",
      stage: "قص",
      pieces: 1200,
      rolls: 12,
      weightKg: 240.5,
      party: "مقصدار المصنع الرئيسي",
      responsible: "أ/ محمود راشد",
      date: "2025-05-10",
      docNumber: "IN-2025-101",
      notes: "استلام كامل من صالة القص"
    },
    {
      id: "TX-02",
      cutNumber: "CUT-101",
      modelName: "تيشرت أوفر سايز صيفي",
      type: "صرف",
      stage: "طباعة",
      pieces: 400,
      rolls: 4,
      weightKg: 80.0,
      party: "مطبعة الأهرام",
      responsible: "م/ حسام الدين",
      date: "2025-05-12",
      docNumber: "OUT-2025-01",
      notes: "دفعة أولى للطباعة الأمامية"
    },
    {
      id: "TX-03",
      cutNumber: "CUT-102",
      modelName: "قميص بولو كاجوال",
      type: "وارد",
      stage: "قص",
      pieces: 850,
      rolls: 9,
      weightKg: 195.0,
      party: "مقصدار المصنع الرئيسي",
      responsible: "أ/ سامح فؤاد",
      date: "2025-05-12",
      docNumber: "IN-2025-102",
      notes: "استلام القماش البيكيه مقصوص"
    },
    {
      id: "TX-04",
      cutNumber: "CUT-102",
      modelName: "قميص بولو كاجوال",
      type: "صرف",
      stage: "تطريز",
      pieces: 850,
      rolls: 9,
      weightKg: 195.0,
      party: "ورشة النجوم للتطريز",
      responsible: "أ/ أشرف صابر",
      date: "2025-05-14",
      docNumber: "OUT-2025-02",
      notes: "صرف كامل الكمية لتطريز اللوجو"
    },
    {
      id: "TX-05",
      cutNumber: "CUT-103",
      modelName: "بنطلون كارجو 6 جيوب",
      type: "وارد",
      stage: "قص",
      pieces: 1500,
      rolls: 16,
      weightKg: 380.0,
      party: "مقصدار الجابردين",
      responsible: "أ/ أحمد عبد المنعم",
      date: "2025-05-15",
      docNumber: "IN-2025-103",
      notes: "تم الفحص ومطابقة الباترون"
    },
    {
      id: "TX-06",
      cutNumber: "CUT-103",
      modelName: "بنطلون كارجو 6 جيوب",
      type: "صرف",
      stage: "خياطة",
      pieces: 600,
      rolls: 6,
      weightKg: 150.0,
      party: "ورشة الأمل للتجميع",
      responsible: "الأسطى ناصر",
      date: "2025-05-17",
      docNumber: "OUT-2025-03",
      notes: "تشغيل الخط رقم 2"
    },
    {
      id: "TX-07",
      cutNumber: "CUT-104",
      modelName: "هودي شتوي مطبوع",
      type: "وارد",
      stage: "قص",
      pieces: 900,
      rolls: 10,
      weightKg: 290.0,
      party: "عنبر القص الشتوي",
      responsible: "أ/ تامر فتحي",
      date: "2025-05-18",
      docNumber: "IN-2025-104",
      notes: "قماش ميلتون درجة أولى"
    }
  ],
  audits: []
};

let appState = {
  cuts: [],
  transactions: [],
  audits: [],
  currentReportId: 1,
  excelSort: { column: 'date', order: 'desc' },
  pendingImportRows: []
};

// Initialize State from LocalStorage
function initDatabase() {
  try {
    const stored = localStorage.getItem(STORAGE_KEY);
    if (stored) {
      const parsed = JSON.parse(stored);
      appState.cuts = parsed.cuts || [];
      appState.transactions = parsed.transactions || [];
      appState.audits = parsed.audits || [];
    } else {
      appState.cuts = [...DEFAULT_DATA.cuts];
      appState.transactions = [...DEFAULT_DATA.transactions];
      appState.audits = [...DEFAULT_DATA.audits];
      saveData();
    }
  } catch (err) {
    console.error("Failed to load local storage:", err);
    appState.cuts = [...DEFAULT_DATA.cuts];
    appState.transactions = [...DEFAULT_DATA.transactions];
    appState.audits = [];
  }
}

function saveData() {
  try {
    localStorage.setItem(STORAGE_KEY, JSON.stringify({
      cuts: appState.cuts,
      transactions: appState.transactions,
      audits: appState.audits
    }));
    updateSidebarKPIs();
    populateCutsDatalists();
  } catch (err) {
    console.error("Save data failed:", err);
  }
}

// ==========================================
// 2. WAREHOUSE CALCULATION ENGINE
// ==========================================

function getCutStats(cutNumber) {
  const cut = appState.cuts.find(c => c.cutNumber === cutNumber);
  const txs = appState.transactions.filter(t => t.cutNumber === cutNumber);

  let inPieces = 0, inRolls = 0, inWeight = 0;
  let outPieces = 0, outRolls = 0, outWeight = 0;

  txs.forEach(t => {
    const pieces = Number(t.pieces) || 0;
    const rolls = Number(t.rolls) || 0;
    const weight = Number(t.weightKg) || 0;

    if (t.type === 'وارد') {
      inPieces += pieces;
      inRolls += rolls;
      inWeight += weight;
    } else if (t.type === 'صرف') {
      outPieces += pieces;
      outRolls += rolls;
      outWeight += weight;
    }
  });

  const balancePieces = inPieces - outPieces;
  const balanceRolls = inRolls - outRolls;
  const balanceWeight = inWeight - outWeight;

  const issuedPercent = inPieces > 0 ? Math.min(100, Math.round((outPieces / inPieces) * 100)) : 0;

  let status = "متوفر";
  if (inPieces === 0) {
    status = "لم يبدأ التوريد";
  } else if (balancePieces <= 0) {
    status = "منصرف بالكامل";
  } else if (outPieces > 0) {
    status = "منصرف جزئياً";
  }

  return {
    cutNumber,
    modelName: cut ? cut.modelName : "",
    inPieces,
    inRolls,
    inWeight,
    outPieces,
    outRolls,
    outWeight,
    balancePieces,
    balanceRolls,
    balanceWeight,
    issuedPercent,
    status
  };
}

function calculateOverallStats(period = 'all') {
  const now = new Date();
  const todayStr = now.toISOString().split('T')[0];
  const currentMonthStr = todayStr.substring(0, 7);

  let filteredTxs = appState.transactions;

  if (period === 'today') {
    filteredTxs = filteredTxs.filter(t => t.date === todayStr);
  } else if (period === 'month') {
    filteredTxs = filteredTxs.filter(t => t.date && t.date.startsWith(currentMonthStr));
  } else if (period === 'week') {
    const oneWeekAgo = new Date();
    oneWeekAgo.setDate(now.getDate() - 7);
    filteredTxs = filteredTxs.filter(t => new Date(t.date) >= oneWeekAgo);
  }

  let inPieces = 0, inRolls = 0, inCount = 0;
  let outPieces = 0, outRolls = 0, outCount = 0;

  filteredTxs.forEach(t => {
    const pieces = Number(t.pieces) || 0;
    const rolls = Number(t.rolls) || 0;
    if (t.type === 'وارد') {
      inPieces += pieces;
      inRolls += rolls;
      inCount++;
    } else if (t.type === 'صرف') {
      outPieces += pieces;
      outRolls += rolls;
      outCount++;
    }
  });

  const netPieces = inPieces - outPieces;
  const netRolls = inRolls - outRolls;

  // Cut status counts
  let availableCuts = 0;
  let partialCuts = 0;
  let emptyCuts = 0;

  appState.cuts.forEach(c => {
    const s = getCutStats(c.cutNumber);
    if (s.balancePieces <= 0 && s.inPieces > 0) {
      emptyCuts++;
    } else if (s.outPieces > 0 && s.balancePieces > 0) {
      partialCuts++;
    } else if (s.inPieces > 0) {
      availableCuts++;
    }
  });

  // Daily & Monthly
  let todayIn = 0, todayOut = 0;
  let monthIn = 0, monthOut = 0;

  appState.transactions.forEach(t => {
    const p = Number(t.pieces) || 0;
    if (t.date === todayStr) {
      if (t.type === 'وارد') todayIn += p;
      if (t.type === 'صرف') todayOut += p;
    }
    if (t.date && t.date.startsWith(currentMonthStr)) {
      if (t.type === 'وارد') monthIn += p;
      if (t.type === 'صرف') monthOut += p;
    }
  });

  return {
    inPieces, inRolls, inCount,
    outPieces, outRolls, outCount,
    netPieces, netRolls,
    availableCuts, partialCuts, emptyCuts,
    totalCuts: appState.cuts.length,
    todayIn, todayOut,
    monthIn, monthOut
  };
}

// ==========================================
// 3. UI VIEW CONTROLLERS & NAVIGATION
// ==========================================

const VIEW_TITLES = {
  dashboard: 'لوحة التحكم والمؤشرات العامة',
  excel: 'شيت المخزن التفاعلي (12 عموداً • A إلى L)',
  cuts: 'أرصدة وحالات القصات المسجلة',
  transactions: 'سجل حركات المخزن التفصيلي',
  reports: 'منظومة التقارير المعتمدة (11 تقرير)',
  audit: 'محاضر الجرد ومطابقة الأرصدة الدفترية',
  tools: 'أدوات إكسل والنسخ الاحتياطي وقاعدة البيانات'
};

function switchView(viewName) {
  // Hide all panels
  document.querySelectorAll('.view-panel').forEach(p => p.classList.add('hidden'));

  // Show target
  const target = document.getElementById(`view-${viewName}`);
  if (target) {
    target.classList.remove('hidden');
  }

  // Update nav buttons
  document.querySelectorAll('.nav-item').forEach(b => b.classList.remove('active'));
  const activeNav = document.getElementById(`nav-${viewName}`);
  if (activeNav) {
    activeNav.classList.add('active');
  }

  // Update header title
  const titleEl = document.getElementById('currentViewTitle');
  if (titleEl) {
    titleEl.textContent = VIEW_TITLES[viewName] || 'مخازن القصات';
  }

  // Close mobile sidebar if open
  closeMobileSidebar();

  // Trigger view renderers
  if (viewName === 'dashboard') renderDashboard();
  if (viewName === 'excel') renderExcelSheet();
  if (viewName === 'cuts') renderCutsView();
  if (viewName === 'transactions') renderTransactionsView();
  if (viewName === 'reports') renderReportsView();
  if (viewName === 'audit') renderAuditView();
}

function updateSidebarKPIs() {
  const stats = calculateOverallStats('all');
  const sbPieces = document.getElementById('sidebarBalancePieces');
  const sbCuts = document.getElementById('sidebarActiveCuts');

  if (sbPieces) sbPieces.textContent = `${stats.netPieces.toLocaleString()} قطعة`;
  if (sbCuts) sbCuts.textContent = `${stats.totalCuts} قصة`;
}

// ==========================================
// 4. RENDER VIEW 1: DASHBOARD
// ==========================================

function renderDashboard() {
  const period = document.getElementById('dashboardPeriodSelect')?.value || 'all';
  const stats = calculateOverallStats(period);

  document.getElementById('kpiNetPieces').textContent = `${stats.netPieces.toLocaleString()} قطعة`;
  document.getElementById('kpiNetRolls').textContent = `${stats.netRolls.toLocaleString()} ثوب / رول`;

  document.getElementById('kpiInPieces').textContent = `${stats.inPieces.toLocaleString()} قطعة`;
  document.getElementById('kpiInRolls').textContent = `${stats.inRolls.toLocaleString()} ثوب`;
  document.getElementById('kpiInCount').textContent = `${stats.inCount} عملية وارد`;

  document.getElementById('kpiOutPieces').textContent = `${stats.outPieces.toLocaleString()} قطعة`;
  document.getElementById('kpiOutRolls').textContent = `${stats.outRolls.toLocaleString()} ثوب`;
  document.getElementById('kpiOutCount').textContent = `${stats.outCount} عملية صرف`;

  document.getElementById('kpiTotalCuts').textContent = stats.totalCuts;
  document.getElementById('kpiCutsStatusMini').textContent = `${stats.availableCuts} متوفرة بالمخزن`;

  document.getElementById('statAvailableCuts').textContent = stats.availableCuts;
  document.getElementById('statPartialCuts').textContent = stats.partialCuts;
  document.getElementById('statEmptyCuts').textContent = stats.emptyCuts;

  // Distribution progress bar
  const total = stats.totalCuts || 1;
  const pAvail = Math.round((stats.availableCuts / total) * 100);
  const pPart = Math.round((stats.partialCuts / total) * 100);
  const pEmpty = Math.max(0, 100 - pAvail - pPart);

  document.getElementById('progressAvailableBar').style.width = `${pAvail}%`;
  document.getElementById('progressPartialBar').style.width = `${pPart}%`;
  document.getElementById('progressEmptyBar').style.width = `${pEmpty}%`;
  
  const overallPercent = stats.inPieces > 0 ? Math.round((stats.outPieces / stats.inPieces) * 100) : 0;
  document.getElementById('overallIssuedPercent').textContent = `نسبة الصرف الكلية: ${overallPercent}%`;

  document.getElementById('dashTodayIn').textContent = `${stats.todayIn.toLocaleString()} قطعة`;
  document.getElementById('dashTodayOut').textContent = `${stats.todayOut.toLocaleString()} قطعة`;
  document.getElementById('dashMonthIn').textContent = `${stats.monthIn.toLocaleString()} قطعة`;
  document.getElementById('dashMonthOut').textContent = `${stats.monthOut.toLocaleString()} قطعة`;

  // Recent transactions table
  const recentBody = document.getElementById('recentTransactionsTableBody');
  if (!recentBody) return;

  const recent = [...appState.transactions].reverse().slice(0, 5);

  if (recent.length === 0) {
    recentBody.innerHTML = `<tr><td colspan="8" class="p-6 text-center text-gray-400">لا توجد حركات مسجلة بالمخزن حالياً</td></tr>`;
    return;
  }

  recentBody.innerHTML = recent.map(t => {
    const isIncoming = t.type === 'وارد';
    const badgeClass = isIncoming ? 'bg-green-100 text-green-800' : 'bg-red-100 text-red-800';
    return `
      <tr class="hover:bg-gray-50 transition">
        <td class="p-3 font-bold text-gray-800">${t.cutNumber}</td>
        <td class="p-3 font-semibold text-gray-700">${t.modelName}</td>
        <td class="p-3">
          <span class="inline-flex items-center px-2 py-0.5 rounded text-[11px] font-bold ${badgeClass}">
            ${isIncoming ? '<i class="fa-solid fa-arrow-down ml-1"></i> وارد' : '<i class="fa-solid fa-arrow-up ml-1"></i> صرف'}
          </span>
        </td>
        <td class="p-3 text-gray-600">${t.stage || '-'}</td>
        <td class="p-3 font-bold ${isIncoming ? 'text-green-700' : 'text-red-700'}">${Number(t.pieces).toLocaleString()}</td>
        <td class="p-3 text-gray-600">${t.party || '-'}</td>
        <td class="p-3 text-gray-500 font-mono text-[11px]">${t.date || '-'}</td>
        <td class="p-3 text-center">
          <button onclick="deleteTransaction('${t.id}')" class="text-red-500 hover:text-red-700 p-1" title="حذف">
            <i class="fa-solid fa-trash-can"></i>
          </button>
        </td>
      </tr>
    `;
  }).join('');
}

// ==========================================
// 5. RENDER VIEW 2: EXCEL SHEET VIEW (12 COLS)
// ==========================================

function renderExcelSheet() {
  const query = (document.getElementById('excelSearchInput')?.value || '').trim().toLowerCase();
  const typeFilter = document.getElementById('excelTypeFilter')?.value || 'all';

  let list = [...appState.transactions];

  if (typeFilter !== 'all') {
    list = list.filter(t => t.type === typeFilter);
  }

  if (query) {
    list = list.filter(t => 
      (t.cutNumber && t.cutNumber.toLowerCase().includes(query)) ||
      (t.modelName && t.modelName.toLowerCase().includes(query)) ||
      (t.party && t.party.toLowerCase().includes(query)) ||
      (t.responsible && t.responsible.toLowerCase().includes(query)) ||
      (t.docNumber && t.docNumber.toLowerCase().includes(query)) ||
      (t.stage && t.stage.toLowerCase().includes(query))
    );
  }

  // Sorting
  if (appState.excelSort.column) {
    const col = appState.excelSort.column;
    const order = appState.excelSort.order === 'asc' ? 1 : -1;
    list.sort((a, b) => {
      const valA = a[col] || '';
      const valB = b[col] || '';
      if (typeof valA === 'number') return (valA - valB) * order;
      return String(valA).localeCompare(String(valB)) * order;
    });
  }

  const tbody = document.getElementById('excelTableBody');
  if (!tbody) return;

  let totalPiecesIn = 0, totalPiecesOut = 0;
  let totalRolls = 0, totalWeight = 0;

  appState.transactions.forEach(t => {
    const p = Number(t.pieces) || 0;
    const r = Number(t.rolls) || 0;
    const w = Number(t.weightKg) || 0;
    if (t.type === 'وارد') totalPiecesIn += p;
    if (t.type === 'صرف') totalPiecesOut += p;
    totalRolls += r;
    totalWeight += w;
  });

  const netWarehouseBalance = totalPiecesIn - totalPiecesOut;

  // Update Formula Bar
  const formulaEl = document.getElementById('formulaResultText');
  if (formulaEl) {
    formulaEl.textContent = `صافي الرصيد = (${totalPiecesIn.toLocaleString()} وارد) - (${totalPiecesOut.toLocaleString()} منصرف) = ${netWarehouseBalance.toLocaleString()} قطعة`;
  }

  // Update Footer totals
  document.getElementById('excelTotalPiecesCell').textContent = netWarehouseBalance.toLocaleString();
  document.getElementById('excelTotalRollsCell').textContent = totalRolls.toLocaleString();
  document.getElementById('excelTotalWeightCell').textContent = `${totalWeight.toFixed(1)} كجم`;
  document.getElementById('excelNetBalanceSummary').textContent = `صافي رصيد المخزن: ${netWarehouseBalance.toLocaleString()} قطعة`;

  if (list.length === 0) {
    tbody.innerHTML = `<tr><td colspan="14" class="p-8 text-center text-gray-400">لا توجد سجلات تطابق معايير البحث في شيت المخزن</td></tr>`;
    return;
  }

  tbody.innerHTML = list.map((t, index) => {
    const isIncoming = t.type === 'وارد';
    const typeBadge = isIncoming 
      ? '<span class="px-2 py-0.5 rounded bg-green-100 text-green-800 font-bold text-[11px]">+ وارد</span>' 
      : '<span class="px-2 py-0.5 rounded bg-red-100 text-red-800 font-bold text-[11px]">- صرف</span>';

    return `
      <tr class="hover:bg-green-50 transition border-b border-gray-200">
        <td class="p-2 border-l border-gray-200 text-center font-mono text-gray-500 bg-gray-50">${index + 1}</td>
        <td class="p-2 border-l border-gray-200 font-bold text-gray-900">${t.cutNumber}</td>
        <td class="p-2 border-l border-gray-200 font-semibold text-gray-800">${t.modelName}</td>
        <td class="p-2 border-l border-gray-200 text-center">${typeBadge}</td>
        <td class="p-2 border-l border-gray-200 text-center text-gray-700">${t.stage || '-'}</td>
        <td class="p-2 border-l border-gray-200 text-center font-black ${isIncoming ? 'text-green-700' : 'text-red-700'} bg-gray-50/50">
          ${isIncoming ? '+' : '-'}${Number(t.pieces).toLocaleString()}
        </td>
        <td class="p-2 border-l border-gray-200 text-center font-semibold text-gray-700">${t.rolls || 0}</td>
        <td class="p-2 border-l border-gray-200 text-center text-gray-600">${t.weightKg || 0}</td>
        <td class="p-2 border-l border-gray-200 text-gray-700">${t.party || '-'}</td>
        <td class="p-2 border-l border-gray-200 text-gray-700">${t.responsible || '-'}</td>
        <td class="p-2 border-l border-gray-200 text-center font-mono text-gray-600">${t.date || '-'}</td>
        <td class="p-2 border-l border-gray-200 text-center font-mono text-gray-600">${t.docNumber || '-'}</td>
        <td class="p-2 border-l border-gray-200 text-gray-500 max-w-xs truncate" title="${t.notes || ''}">${t.notes || '-'}</td>
        <td class="p-2 text-center">
          <button onclick="deleteTransaction('${t.id}')" class="text-red-500 hover:text-red-700 p-1 transition" title="حذف الصف">
            <i class="fa-solid fa-trash-can"></i>
          </button>
        </td>
      </tr>
    `;
  }).join('');
}

function sortExcel(column) {
  if (appState.excelSort.column === column) {
    appState.excelSort.order = appState.excelSort.order === 'asc' ? 'desc' : 'asc';
  } else {
    appState.excelSort.column = column;
    appState.excelSort.order = 'asc';
  }
  renderExcelSheet();
}

// ==========================================
// 6. RENDER VIEW 3: CUTS BALANCES
// ==========================================

function renderCutsView() {
  const query = (document.getElementById('cutsSearchInput')?.value || '').trim().toLowerCase();
  const filter = document.getElementById('cutsFilterStatus')?.value || 'all';

  let cuts = [...appState.cuts];

  if (query) {
    cuts = cuts.filter(c => 
      c.cutNumber.toLowerCase().includes(query) || 
      c.modelName.toLowerCase().includes(query) ||
      (c.fabricType && c.fabricType.toLowerCase().includes(query))
    );
  }

  const container = document.getElementById('cutsCardsGrid');
  if (!container) return;

  const cardsHtml = cuts.map(c => {
    const stats = getCutStats(c.cutNumber);

    if (filter === 'available' && stats.balancePieces <= 0) return '';
    if (filter === 'partial' && (stats.outPieces === 0 || stats.balancePieces <= 0)) return '';
    if (filter === 'empty' && stats.balancePieces > 0) return '';

    let statusColor = 'bg-green-100 text-green-800 border-green-300';
    if (stats.status === 'منصرف جزئياً') statusColor = 'bg-amber-100 text-amber-800 border-amber-300';
    if (stats.status === 'منصرف بالكامل') statusColor = 'bg-gray-100 text-gray-700 border-gray-300';

    return `
      <div class="bg-white rounded-xl shadow-sm border border-gray-200 p-4 space-y-3 hover:shadow-md transition">
        <div class="flex items-start justify-between">
          <div>
            <span class="text-xs font-mono font-bold text-excel-700 bg-excel-50 px-2.5 py-1 rounded-md border border-excel-200">
              ${c.cutNumber}
            </span>
            <h3 class="font-bold text-base text-gray-900 mt-1.5">${c.modelName}</h3>
            <p class="text-xs text-gray-500">${c.fabricType || 'قماش عام'} • ${c.color || 'ألوان متعددة'}</p>
          </div>
          <span class="text-[11px] font-bold px-2 py-1 rounded-full border ${statusColor}">
            ${stats.status}
          </span>
        </div>

        <!-- Metrics Grid -->
        <div class="grid grid-cols-3 gap-2 bg-gray-50 p-2.5 rounded-lg border border-gray-100 text-center text-xs">
          <div>
            <span class="text-gray-400 block text-[10px]">إجمالي الوارد</span>
            <strong class="text-green-700 font-bold">${stats.inPieces.toLocaleString()}</strong>
          </div>
          <div>
            <span class="text-gray-400 block text-[10px]">المنصرف للورش</span>
            <strong class="text-red-700 font-bold">${stats.outPieces.toLocaleString()}</strong>
          </div>
          <div>
            <span class="text-gray-400 block text-[10px]">الرصيد المتبقي</span>
            <strong class="text-excel-700 font-black text-sm">${stats.balancePieces.toLocaleString()}</strong>
          </div>
        </div>

        <!-- Progress Bar -->
        <div>
          <div class="flex justify-between text-[11px] text-gray-500 mb-1">
            <span>نسبة الصرف للتشغيل</span>
            <strong class="text-gray-700 font-bold">${stats.issuedPercent}%</strong>
          </div>
          <div class="w-full h-2 bg-gray-200 rounded-full overflow-hidden">
            <div class="h-full ${stats.issuedPercent === 100 ? 'bg-gray-500' : 'bg-excel-600'} transition-all duration-300" style="width: ${stats.issuedPercent}%"></div>
          </div>
        </div>

        <!-- Footer Actions -->
        <div class="pt-2 border-t border-gray-100 flex items-center justify-between text-xs">
          <span class="text-gray-400 text-[11px]">الموسم: ${c.season || '2025'}</span>
          <div class="flex items-center gap-1">
            <button onclick="openEditCutModal('${c.cutNumber}')" class="text-blue-600 hover:text-blue-800 p-1.5 rounded hover:bg-blue-50 transition" title="تعديل">
              <i class="fa-solid fa-pen-to-square"></i>
            </button>
            <button onclick="deleteCut('${c.cutNumber}')" class="text-red-500 hover:text-red-700 p-1.5 rounded hover:bg-red-50 transition" title="حذف القصة">
              <i class="fa-solid fa-trash-can"></i>
            </button>
          </div>
        </div>
      </div>
    `;
  }).filter(Boolean).join('');

  if (!cardsHtml) {
    container.innerHTML = `<div class="col-span-full p-8 text-center text-gray-400 bg-white rounded-xl border border-gray-200">لا توجد قصات مطابقة للمعايير المحددة</div>`;
  } else {
    container.innerHTML = cardsHtml;
  }
}

// ==========================================
// 7. RENDER VIEW 4: TRANSACTIONS LOG
// ==========================================

function renderTransactionsView() {
  const query = (document.getElementById('txSearchInput')?.value || '').trim().toLowerCase();
  const typeFilter = document.getElementById('txTypeFilter')?.value || 'all';
  const stageFilter = document.getElementById('txStageFilter')?.value || 'all';

  let list = [...appState.transactions].reverse();

  if (typeFilter !== 'all') {
    list = list.filter(t => t.type === typeFilter);
  }
  if (stageFilter !== 'all') {
    list = list.filter(t => t.stage === stageFilter);
  }
  if (query) {
    list = list.filter(t => 
      t.cutNumber.toLowerCase().includes(query) ||
      t.modelName.toLowerCase().includes(query) ||
      (t.party && t.party.toLowerCase().includes(query)) ||
      (t.docNumber && t.docNumber.toLowerCase().includes(query))
    );
  }

  const tbody = document.getElementById('transactionsTableBody');
  if (!tbody) return;

  if (list.length === 0) {
    tbody.innerHTML = `<tr><td colspan="12" class="p-8 text-center text-gray-400">لا توجد حركات مسجلة مطابقة للبحث</td></tr>`;
    return;
  }

  tbody.innerHTML = list.map(t => {
    const isIncoming = t.type === 'وارد';
    const badge = isIncoming
      ? '<span class="px-2 py-0.5 rounded bg-green-100 text-green-800 font-bold text-xs"><i class="fa-solid fa-arrow-down ml-1"></i> وارد</span>'
      : '<span class="px-2 py-0.5 rounded bg-red-100 text-red-800 font-bold text-xs"><i class="fa-solid fa-arrow-up ml-1"></i> صرف</span>';

    return `
      <tr class="hover:bg-gray-50 transition border-b border-gray-100">
        <td class="p-3 font-bold text-gray-900">${t.cutNumber}</td>
        <td class="p-3 font-semibold text-gray-800">${t.modelName}</td>
        <td class="p-3 text-center">${badge}</td>
        <td class="p-3 text-center text-gray-700">${t.stage || '-'}</td>
        <td class="p-3 text-center font-bold ${isIncoming ? 'text-green-700' : 'text-red-700'}">${Number(t.pieces).toLocaleString()}</td>
        <td class="p-3 text-center text-gray-600">${t.rolls || 0}</td>
        <td class="p-3 text-center text-gray-600">${t.weightKg || 0}</td>
        <td class="p-3 text-gray-700">${t.party || '-'}</td>
        <td class="p-3 text-gray-600">${t.responsible || '-'}</td>
        <td class="p-3 text-center font-mono text-gray-600">${t.date || '-'}</td>
        <td class="p-3 text-center font-mono text-gray-600">${t.docNumber || '-'}</td>
        <td class="p-3 text-center">
          <button onclick="deleteTransaction('${t.id}')" class="text-red-500 hover:text-red-700 p-1.5 rounded hover:bg-red-50 transition" title="حذف الحركة">
            <i class="fa-solid fa-trash-can"></i>
          </button>
        </td>
      </tr>
    `;
  }).join('');
}

// ==========================================
// 8. RENDER VIEW 5: 11 DETAILED REPORTS
// ==========================================

const REPORT_DEFINITIONS = [
  { id: 1, title: 'تقرير حركة المخزن العامة', subtitle: 'كافة عمليات الإدخال والصرف مرتبة زمنياً' },
  { id: 2, title: 'تقرير أرصدة وجرد القصات', subtitle: 'بيان الأرصدة المتبقية ونسب التشغيل وحالة كل قصة' },
  { id: 3, title: 'تقرير الوارد بالتفصيل', subtitle: 'تفاصيل التوريدات من صالات ومقصدارات القص' },
  { id: 4, title: 'تقرير المنصرف بالتفصيل', subtitle: 'أذونات الصرف للورش والمراحل الإنتاجية' },
  { id: 5, title: 'تقرير حسب الموديل', subtitle: 'تجميع إجمالي الوارد والمنصرف والأرصدة لكل موديل' },
  { id: 6, title: 'تقرير حسب نوع القماش والخامة', subtitle: 'إحصائيات استهلاك وتوزيع الأقمشة' },
  { id: 7, title: 'تقرير حسب الورشة والجهة', subtitle: 'حجم التشغيل ومسحوبات كل ورشة ومصنع' },
  { id: 8, title: 'تقرير حسب المرحلة الإنتاجية', subtitle: 'حركات القص والخياطة والطباعة والتطريز' },
  { id: 9, title: 'تقرير جرد المخزن والفروقات', subtitle: 'مطابقة الأرصدة الدفترية والفعلية' },
  { id: 10, title: 'تقرير القصات المنصرفة بالكامل', subtitle: 'أكواد القصات التي تم صرف 100% من كمياتها' },
  { id: 11, title: 'تقرير القصات ذات الرصيد المتاح', subtitle: 'القصات الجاهزة للصرف والتشغيل حالياً' }
];

function selectReport(reportId) {
  appState.currentReportId = reportId;
  document.querySelectorAll('.report-tab-btn').forEach((b, idx) => {
    if (idx + 1 === reportId) b.classList.add('active');
    else b.classList.remove('active');
  });
  renderReportsView();
}

function renderReportsView() {
  const repId = appState.currentReportId || 1;
  const def = REPORT_DEFINITIONS.find(r => r.id === repId) || REPORT_DEFINITIONS[0];

  document.getElementById('reportViewTitle').textContent = def.title;
  document.getElementById('reportViewSubtitle').textContent = def.subtitle;
  document.getElementById('reportDateStamp').textContent = new Date().toLocaleDateString('ar-EG');

  const thead = document.getElementById('reportTableHead');
  const tbody = document.getElementById('reportTableBody');
  const metrics = document.getElementById('reportSummaryMetrics');

  let rowsHtml = '';
  let headHtml = '';
  let metricsHtml = '';

  // Generate Report Content based on Report ID
  if (repId === 1) { // 1. حركة المخزن العامة
    headHtml = `
      <tr>
        <th class="p-3">رقم القصة</th>
        <th class="p-3">الموديل</th>
        <th class="p-3 text-center">نوع الحركة</th>
        <th class="p-3 text-center">المرحلة</th>
        <th class="p-3 text-center">القطع</th>
        <th class="p-3 text-center">الأثواب</th>
        <th class="p-3">الجهة / الورشة</th>
        <th class="p-3 text-center">التاريخ</th>
        <th class="p-3 text-center">المستند</th>
      </tr>
    `;

    const txs = [...appState.transactions];
    document.getElementById('reportRecordCount').textContent = txs.length;

    let totIn = 0, totOut = 0;
    rowsHtml = txs.map(t => {
      if (t.type === 'وارد') totIn += Number(t.pieces) || 0;
      else totOut += Number(t.pieces) || 0;

      return `
        <tr>
          <td class="p-2.5 font-bold">${t.cutNumber}</td>
          <td class="p-2.5">${t.modelName}</td>
          <td class="p-2.5 text-center font-bold ${t.type === 'وارد' ? 'text-green-700' : 'text-red-700'}">${t.type}</td>
          <td class="p-2.5 text-center">${t.stage || '-'}</td>
          <td class="p-2.5 text-center font-bold">${Number(t.pieces).toLocaleString()}</td>
          <td class="p-2.5 text-center">${t.rolls || 0}</td>
          <td class="p-2.5">${t.party || '-'}</td>
          <td class="p-2.5 text-center font-mono">${t.date || '-'}</td>
          <td class="p-2.5 text-center font-mono">${t.docNumber || '-'}</td>
        </tr>
      `;
    }).join('');

    metricsHtml = `
      <div><span class="text-gray-500 block">إجمالي الوارد</span><strong class="text-green-700 font-bold">${totIn.toLocaleString()} قطعة</strong></div>
      <div><span class="text-gray-500 block">إجمالي المنصرف</span><strong class="text-red-700 font-bold">${totOut.toLocaleString()} قطعة</strong></div>
      <div><span class="text-gray-500 block">صافي الرصيد</span><strong class="text-excel-700 font-bold">${(totIn - totOut).toLocaleString()} قطعة</strong></div>
      <div><span class="text-gray-500 block">عدد العمليات</span><strong class="text-gray-800 font-bold">${txs.length} حركة</strong></div>
    `;
  } 
  else if (repId === 2 || repId === 10 || repId === 11) { // 2. أرصدة وجرد القصات / 10. منصرفة / 11. متاحة
    headHtml = `
      <tr>
        <th class="p-3">رقم القصة</th>
        <th class="p-3">اسم الموديل</th>
        <th class="p-3">نوع القماش</th>
        <th class="p-3 text-center">الوارد</th>
        <th class="p-3 text-center">المنصرف</th>
        <th class="p-3 text-center">الرصيد المتاح</th>
        <th class="p-3 text-center">نسبة الصرف</th>
        <th class="p-3 text-center">الحالة</th>
      </tr>
    `;

    let cuts = appState.cuts.map(c => {
      const stats = getCutStats(c.cutNumber);
      return { ...c, ...stats };
    });

    if (repId === 10) cuts = cuts.filter(c => c.balancePieces <= 0 && c.inPieces > 0);
    if (repId === 11) cuts = cuts.filter(c => c.balancePieces > 0);

    document.getElementById('reportRecordCount').textContent = cuts.length;

    let totBal = 0;
    rowsHtml = cuts.map(c => {
      totBal += c.balancePieces;
      return `
        <tr>
          <td class="p-2.5 font-bold">${c.cutNumber}</td>
          <td class="p-2.5 font-semibold">${c.modelName}</td>
          <td class="p-2.5 text-gray-600">${c.fabricType || '-'}</td>
          <td class="p-2.5 text-center text-green-700 font-bold">${c.inPieces.toLocaleString()}</td>
          <td class="p-2.5 text-center text-red-700 font-bold">${c.outPieces.toLocaleString()}</td>
          <td class="p-2.5 text-center text-excel-700 font-black">${c.balancePieces.toLocaleString()}</td>
          <td class="p-2.5 text-center font-bold">${c.issuedPercent}%</td>
          <td class="p-2.5 text-center"><span class="px-2 py-0.5 rounded bg-gray-100 text-gray-800 text-[11px] font-bold">${c.status}</span></td>
        </tr>
      `;
    }).join('');

    metricsHtml = `
      <div><span class="text-gray-500 block">إجمالي القصات</span><strong class="text-gray-800 font-bold">${cuts.length} قصة</strong></div>
      <div><span class="text-gray-500 block">مجموع الأرصدة المتاحة</span><strong class="text-excel-700 font-bold">${totBal.toLocaleString()} قطعة</strong></div>
    `;
  }
  else if (repId === 3 || repId === 4) { // 3. وارد بالتفصيل / 4. منصرف بالتفصيل
    const targetType = repId === 3 ? 'وارد' : 'صرف';
    headHtml = `
      <tr>
        <th class="p-3">رقم القصة</th>
        <th class="p-3">الموديل</th>
        <th class="p-3 text-center">القطع</th>
        <th class="p-3 text-center">الأثواب</th>
        <th class="p-3 text-center">الوزن كجم</th>
        <th class="p-3">${repId === 3 ? 'جهة التوريد' : 'الورشة المنصرف لها'}</th>
        <th class="p-3">المسؤول</th>
        <th class="p-3 text-center">التاريخ</th>
        <th class="p-3 text-center">المستند</th>
      </tr>
    `;

    const txs = appState.transactions.filter(t => t.type === targetType);
    document.getElementById('reportRecordCount').textContent = txs.length;

    let totP = 0, totR = 0, totW = 0;
    rowsHtml = txs.map(t => {
      totP += Number(t.pieces) || 0;
      totR += Number(t.rolls) || 0;
      totW += Number(t.weightKg) || 0;
      return `
        <tr>
          <td class="p-2.5 font-bold">${t.cutNumber}</td>
          <td class="p-2.5">${t.modelName}</td>
          <td class="p-2.5 text-center font-bold ${repId === 3 ? 'text-green-700' : 'text-red-700'}">${Number(t.pieces).toLocaleString()}</td>
          <td class="p-2.5 text-center">${t.rolls || 0}</td>
          <td class="p-2.5 text-center">${t.weightKg || 0}</td>
          <td class="p-2.5">${t.party || '-'}</td>
          <td class="p-2.5">${t.responsible || '-'}</td>
          <td class="p-2.5 text-center font-mono">${t.date || '-'}</td>
          <td class="p-2.5 text-center font-mono">${t.docNumber || '-'}</td>
        </tr>
      `;
    }).join('');

    metricsHtml = `
      <div><span class="text-gray-500 block">إجمالي القطع</span><strong class="${repId === 3 ? 'text-green-700' : 'text-red-700'} font-bold">${totP.toLocaleString()} قطعة</strong></div>
      <div><span class="text-gray-500 block">إجمالي الأثواب</span><strong class="text-gray-800 font-bold">${totR.toLocaleString()} ثوب</strong></div>
      <div><span class="text-gray-500 block">إجمالي الوزن</span><strong class="text-gray-800 font-bold">${totW.toFixed(1)} كجم</strong></div>
      <div><span class="text-gray-500 block">عدد الأذونات</span><strong class="text-gray-800 font-bold">${txs.length} إذن</strong></div>
    `;
  }
  else if (repId === 5) { // 5. حسب الموديل
    headHtml = `
      <tr>
        <th class="p-3">اسم الموديل</th>
        <th class="p-3 text-center">عدد القصات</th>
        <th class="p-3 text-center">إجمالي الوارد</th>
        <th class="p-3 text-center">إجمالي المنصرف</th>
        <th class="p-3 text-center">الرصيد الحالي</th>
      </tr>
    `;

    const modelMap = {};
    appState.cuts.forEach(c => {
      const stats = getCutStats(c.cutNumber);
      const name = c.modelName || 'غير محدد';
      if (!modelMap[name]) modelMap[name] = { count: 0, in: 0, out: 0, balance: 0 };
      modelMap[name].count++;
      modelMap[name].in += stats.inPieces;
      modelMap[name].out += stats.outPieces;
      modelMap[name].balance += stats.balancePieces;
    });

    const entries = Object.entries(modelMap);
    document.getElementById('reportRecordCount').textContent = entries.length;

    rowsHtml = entries.map(([name, data]) => `
      <tr>
        <td class="p-2.5 font-bold text-gray-900">${name}</td>
        <td class="p-2.5 text-center">${data.count}</td>
        <td class="p-2.5 text-center text-green-700 font-bold">${data.in.toLocaleString()}</td>
        <td class="p-2.5 text-center text-red-700 font-bold">${data.out.toLocaleString()}</td>
        <td class="p-2.5 text-center text-excel-700 font-black">${data.balance.toLocaleString()}</td>
      </tr>
    `).join('');

    metricsHtml = `<div><span class="text-gray-500 block">عدد الموديلات</span><strong class="text-gray-800 font-bold">${entries.length} موديل</strong></div>`;
  }
  else if (repId === 7 || repId === 8) { // 7. الورشة / 8. المرحلة
    const isWorkshop = repId === 7;
    const groupKey = isWorkshop ? 'party' : 'stage';

    headHtml = `
      <tr>
        <th class="p-3">${isWorkshop ? 'الجهة / الورشة' : 'المرحلة الإنتاجية'}</th>
        <th class="p-3 text-center">عدد العمليات</th>
        <th class="p-3 text-center">القطع المنصرفة</th>
        <th class="p-3 text-center">الأثواب</th>
      </tr>
    `;

    const map = {};
    appState.transactions.filter(t => t.type === 'صرف').forEach(t => {
      const key = t[groupKey] || 'غير محدد';
      if (!map[key]) map[key] = { count: 0, pieces: 0, rolls: 0 };
      map[key].count++;
      map[key].pieces += Number(t.pieces) || 0;
      map[key].rolls += Number(t.rolls) || 0;
    });

    const entries = Object.entries(map);
    document.getElementById('reportRecordCount').textContent = entries.length;

    rowsHtml = entries.map(([key, data]) => `
      <tr>
        <td class="p-2.5 font-bold">${key}</td>
        <td class="p-2.5 text-center">${data.count}</td>
        <td class="p-2.5 text-center text-red-700 font-bold">${data.pieces.toLocaleString()}</td>
        <td class="p-2.5 text-center">${data.rolls}</td>
      </tr>
    `).join('');

    metricsHtml = `<div><span class="text-gray-500 block">${isWorkshop ? 'عدد الورش' : 'عدد المراحل'}</span><strong class="text-gray-800 font-bold">${entries.length}</strong></div>`;
  }
  else { // 6 or 9 or fallback
    headHtml = `
      <tr>
        <th class="p-3">رقم القصة</th>
        <th class="p-3">الموديل</th>
        <th class="p-3 text-center">الرصيد الدفتري</th>
        <th class="p-3 text-center">حالة الرصيد</th>
      </tr>
    `;
    rowsHtml = appState.cuts.map(c => {
      const s = getCutStats(c.cutNumber);
      return `
        <tr>
          <td class="p-2.5 font-bold">${c.cutNumber}</td>
          <td class="p-2.5">${c.modelName}</td>
          <td class="p-2.5 text-center font-bold text-excel-700">${s.balancePieces.toLocaleString()}</td>
          <td class="p-2.5 text-center">${s.status}</td>
        </tr>
      `;
    }).join('');
    metricsHtml = `<div><span class="text-gray-500 block">إجمالي السجلات</span><strong class="text-gray-800 font-bold">${appState.cuts.length}</strong></div>`;
  }

  thead.innerHTML = headHtml;
  tbody.innerHTML = rowsHtml;
  metrics.innerHTML = metricsHtml;
}

function printCurrentReport() {
  window.print();
}

function exportReportToExcel() {
  const repId = appState.currentReportId || 1;
  const def = REPORT_DEFINITIONS.find(r => r.id === repId) || REPORT_DEFINITIONS[0];

  const table = document.querySelector('#printableReportArea table');
  if (!table) return;

  let csvContent = "\uFEFF"; // UTF-8 BOM for Arabic Excel
  const rows = table.querySelectorAll('tr');

  rows.forEach(row => {
    const cols = row.querySelectorAll('th, td');
    const rowData = [];
    cols.forEach(col => {
      let text = col.innerText.replace(/"/g, '""').trim();
      rowData.push(`"${text}"`);
    });
    csvContent += rowData.join(",") + "\r\n";
  });

  downloadFile(csvContent, `${def.title}_${new Date().toISOString().split('T')[0]}.csv`, 'text/csv;charset=utf-8;');
  showToast('تم تصدير التقرير إلى Excel بنجاح', 'success');
}

// ==========================================
// 9. RENDER VIEW 6: AUDIT & RECONCILIATION
// ==========================================

function renderAuditView() {
  const tbody = document.getElementById('auditTableBody');
  if (!tbody) return;

  const dateInput = document.getElementById('auditDateInput');
  if (dateInput && !dateInput.value) {
    dateInput.value = new Date().toISOString().split('T')[0];
  }

  tbody.innerHTML = appState.cuts.map(c => {
    const stats = getCutStats(c.cutNumber);
    return `
      <tr class="hover:bg-gray-50 transition border-b border-gray-100" data-cut="${c.cutNumber}">
        <td class="p-3 font-bold text-gray-900">${c.cutNumber}</td>
        <td class="p-3 font-semibold text-gray-800">${c.modelName}</td>
        <td class="p-3 text-center font-black text-excel-700 text-sm book-balance-cell">${stats.balancePieces}</td>
        <td class="p-3 text-center">
          <input type="number" class="actual-balance-input w-24 bg-gray-50 border border-gray-300 rounded p-1.5 text-center font-bold focus:ring-2 focus:ring-excel-600 focus:outline-none" 
            value="${stats.balancePieces}" min="0" oninput="calculateAuditRowDifference(this)">
        </td>
        <td class="p-3 text-center font-bold text-sm diff-cell text-green-700">0</td>
        <td class="p-3 text-center status-badge-cell">
          <span class="px-2 py-1 rounded-full bg-green-100 text-green-800 font-bold text-xs">مطابق</span>
        </td>
        <td class="p-3">
          <input type="text" placeholder="سبب الفرق إن وجد..." class="audit-notes-input w-full bg-gray-50 border border-gray-200 rounded p-1.5 text-xs focus:ring-2 focus:ring-excel-600 focus:outline-none">
        </td>
      </tr>
    `;
  }).join('');
}

function calculateAuditRowDifference(inputEl) {
  const tr = inputEl.closest('tr');
  const bookBalance = Number(tr.querySelector('.book-balance-cell').innerText) || 0;
  const actualVal = Number(inputEl.value) || 0;
  const diff = actualVal - bookBalance; // الفرق = الفعلي - الدفتري

  const diffCell = tr.querySelector('.diff-cell');
  const statusCell = tr.querySelector('.status-badge-cell');

  diffCell.textContent = (diff > 0 ? `+${diff}` : `${diff}`);

  if (diff === 0) {
    diffCell.className = 'p-3 text-center font-bold text-sm diff-cell text-green-700';
    statusCell.innerHTML = '<span class="px-2 py-1 rounded-full bg-green-100 text-green-800 font-bold text-xs">مطابق</span>';
  } else if (diff > 0) {
    diffCell.className = 'p-3 text-center font-bold text-sm diff-cell text-blue-700';
    statusCell.innerHTML = '<span class="px-2 py-1 rounded-full bg-blue-100 text-blue-800 font-bold text-xs">زيادة</span>';
  } else {
    diffCell.className = 'p-3 text-center font-bold text-sm diff-cell text-red-700';
    statusCell.innerHTML = '<span class="px-2 py-1 rounded-full bg-red-100 text-red-800 font-bold text-xs">عجز</span>';
  }
}

function saveAuditProtocol() {
  const auditor = document.getElementById('auditAuditorName')?.value || 'أمين المخزن';
  const date = document.getElementById('auditDateInput')?.value || new Date().toISOString().split('T')[0];
  const generalNotes = document.getElementById('auditGeneralNotes')?.value || '';

  const trs = document.querySelectorAll('#auditTableBody tr');
  const records = [];

  trs.forEach(tr => {
    const cutNumber = tr.getAttribute('data-cut');
    const bookBalance = Number(tr.querySelector('.book-balance-cell').innerText) || 0;
    const actualBalance = Number(tr.querySelector('.actual-balance-input').value) || 0;
    const diff = actualBalance - bookBalance;
    const notes = tr.querySelector('.audit-notes-input').value || '';
    const status = diff === 0 ? 'مطابق' : (diff > 0 ? 'زيادة' : 'عجز');

    records.push({
      cutNumber,
      bookBalance,
      actualBalance,
      diff,
      status,
      notes
    });
  });

  appState.audits.push({
    id: `AUDIT-${Date.now()}`,
    date,
    auditor,
    generalNotes,
    records
  });

  saveData();
  showToast('تم اعتماد وحفظ محضر الجرد بنجاح في قاعدة البيانات', 'success');
}

function exportAuditToExcel() {
  let csv = "\uFEFFرقم القصة,اسم الموديل,الرصيد الدفتري,الرصيد الفعلي,الفرق,حالة المطابقة,الملاحظات\r\n";
  const trs = document.querySelectorAll('#auditTableBody tr');

  trs.forEach(tr => {
    const cut = tr.children[0].innerText.trim();
    const model = tr.children[1].innerText.trim();
    const book = tr.children[2].innerText.trim();
    const actual = tr.querySelector('.actual-balance-input').value.trim();
    const diff = tr.children[4].innerText.trim();
    const status = tr.children[5].innerText.trim();
    const notes = tr.querySelector('.audit-notes-input').value.trim();

    csv += `"${cut}","${model}",${book},${actual},"${diff}","${status}","${notes}"\r\n`;
  });

  downloadFile(csv, `محضر_جرد_المخزن_${new Date().toISOString().split('T')[0]}.csv`, 'text/csv;charset=utf-8;');
  showToast('تم تصدير شيت الجرد إلى Excel بنجاح', 'success');
}

// ==========================================
// 10. STOCK IN / STOCK OUT LOGIC & MODALS
// ==========================================

function populateCutsDatalists() {
  const datalist = document.getElementById('cutsListDatalist');
  const selectOut = document.getElementById('outCutNumberSelect');

  if (datalist) {
    datalist.innerHTML = appState.cuts.map(c => `<option value="${c.cutNumber}">${c.modelName}</option>`).join('');
  }

  if (selectOut) {
    selectOut.innerHTML = '<option value="">-- اختر القصة للصرف --</option>' + 
      appState.cuts.map(c => {
        const stats = getCutStats(c.cutNumber);
        return `<option value="${c.cutNumber}">${c.cutNumber} - ${c.modelName} (المتاح: ${stats.balancePieces} قطعة)</option>`;
      }).join('');
  }
}

function autoFillModelName(cutNumber, targetInputId) {
  const cut = appState.cuts.find(c => c.cutNumber.toUpperCase() === cutNumber.trim().toUpperCase());
  const input = document.getElementById(targetInputId);
  if (cut && input && !input.value) {
    input.value = cut.modelName;
  }
}

function handleStockInSubmit(e) {
  e.preventDefault();

  const cutNumber = document.getElementById('inCutNumber').value.trim().toUpperCase();
  const modelName = document.getElementById('inModelName').value.trim();
  const fabricType = document.getElementById('inFabricType').value.trim();
  const color = document.getElementById('inColor').value.trim();
  const pieces = Number(document.getElementById('inPieces').value) || 0;
  const rolls = Number(document.getElementById('inRolls').value) || 0;
  const weightKg = Number(document.getElementById('inWeight').value) || 0;
  const party = document.getElementById('inParty').value.trim();
  const responsible = document.getElementById('inResponsible').value.trim();
  const date = document.getElementById('inDate').value || new Date().toISOString().split('T')[0];
  const docNumber = document.getElementById('inDocNumber').value.trim() || `IN-${Date.now().toString().slice(-4)}`;
  const notes = document.getElementById('inNotes').value.trim();

  if (!cutNumber || !modelName || pieces <= 0) {
    showToast('يرجى ملء جميع الحقول الإلزامية وعدد القطع', 'error');
    return;
  }

  // Create Cut if not already registered
  let existingCut = appState.cuts.find(c => c.cutNumber === cutNumber);
  if (!existingCut) {
    existingCut = {
      cutNumber,
      modelName,
      fabricType,
      color,
      season: 'صيف 2025',
      targetPieces: pieces,
      notes: notes || 'تم إنشاؤها مع أول إذن وارد',
      createdAt: date
    };
    appState.cuts.push(existingCut);
  }

  // Create In Transaction
  const newTx = {
    id: `TX-IN-${Date.now()}`,
    cutNumber,
    modelName,
    type: 'وارد',
    stage: 'قص',
    pieces,
    rolls,
    weightKg,
    party: party || 'المقصدار',
    responsible: responsible || 'أمين المخزن',
    date,
    docNumber,
    notes
  };

  appState.transactions.push(newTx);
  saveData();

  closeModal('stockInModal');
  document.getElementById('stockInForm').reset();
  showToast(`تم تسجيل الوارد بنجاح (+${pieces.toLocaleString()} قطعة لقصة ${cutNumber})`, 'success');

  renderDashboard();
  renderExcelSheet();
  renderCutsView();
}

function handleOutCutSelected() {
  const cutNumber = document.getElementById('outCutNumberSelect').value;
  const banner = document.getElementById('outCutBalanceBanner');
  const availPiecesEl = document.getElementById('outAvailablePiecesText');
  const availRollsEl = document.getElementById('outAvailableRollsText');
  const modelBadge = document.getElementById('outModelNameBadge');

  if (!cutNumber) {
    availPiecesEl.textContent = '0 قطعة';
    availRollsEl.textContent = '(0 ثوب)';
    modelBadge.textContent = '-';
    validateOutgoingBalance();
    return;
  }

  const stats = getCutStats(cutNumber);
  availPiecesEl.textContent = `${stats.balancePieces.toLocaleString()} قطعة`;
  availRollsEl.textContent = `(${stats.balanceRolls.toLocaleString()} ثوب)`;
  modelBadge.textContent = stats.modelName;

  validateOutgoingBalance();
}

/**
 * STRICT NEGATIVE BALANCE VALIDATION:
 * Prevents any stock out greater than the available balance
 */
function validateOutgoingBalance() {
  const cutNumber = document.getElementById('outCutNumberSelect')?.value;
  const piecesInput = document.getElementById('outPieces');
  const errorBanner = document.getElementById('outErrorBanner');
  const errorMsg = document.getElementById('outErrorMessage');
  const submitBtn = document.getElementById('confirmStockOutBtn');

  if (!cutNumber || !piecesInput) {
    if (errorBanner) errorBanner.classList.add('hidden');
    if (submitBtn) submitBtn.disabled = false;
    return true;
  }

  const stats = getCutStats(cutNumber);
  const requestedPieces = Number(piecesInput.value) || 0;

  if (requestedPieces > stats.balancePieces) {
    errorBanner.classList.remove('hidden');
    errorMsg.textContent = `عذراً! الكمية المطلوبة للصرف (${requestedPieces.toLocaleString()} قطعة) أكبر من الرصيد الدفتري المتاح حالياً بالمخزن (${stats.balancePieces.toLocaleString()} قطعة). لا يمكن تسجيل رصيد سالب!`;
    submitBtn.disabled = true;
    submitBtn.classList.add('opacity-50', 'cursor-not-allowed');
    return false;
  } else {
    errorBanner.classList.add('hidden');
    submitBtn.disabled = false;
    submitBtn.classList.remove('opacity-50', 'cursor-not-allowed');
    return true;
  }
}

function handleStockOutSubmit(e) {
  e.preventDefault();

  const cutNumber = document.getElementById('outCutNumberSelect').value;
  const pieces = Number(document.getElementById('outPieces').value) || 0;
  const rolls = Number(document.getElementById('outRolls').value) || 0;
  const weightKg = Number(document.getElementById('outWeight').value) || 0;
  const party = document.getElementById('outParty').value.trim();
  const stage = document.getElementById('outStage').value;
  const receiver = document.getElementById('outReceiver').value.trim();
  const storekeeper = document.getElementById('outStorekeeper').value.trim();
  const date = document.getElementById('outDate').value || new Date().toISOString().split('T')[0];
  const docNumber = document.getElementById('outDocNumber').value.trim() || `OUT-${Date.now().toString().slice(-4)}`;
  const notes = document.getElementById('outNotes').value.trim();

  if (!cutNumber) {
    showToast('يرجى اختيار القصة المراد الصرف منها', 'error');
    return;
  }

  const stats = getCutStats(cutNumber);

  // STRICT NEGATIVE BALANCE CHECK
  if (pieces <= 0 || pieces > stats.balancePieces) {
    showToast(`لا يمكن الصرف! الكمية المطلوبة أكبر من الرصيد المتاح (${stats.balancePieces})`, 'error');
    validateOutgoingBalance();
    return;
  }

  const newTx = {
    id: `TX-OUT-${Date.now()}`,
    cutNumber,
    modelName: stats.modelName,
    type: 'صرف',
    stage: stage || 'خياطة',
    pieces,
    rolls,
    weightKg,
    party: party || 'ورشة التشغيل',
    responsible: receiver || storekeeper,
    date,
    docNumber,
    notes: `${notes} [المُسلّم: ${storekeeper}]`
  };

  appState.transactions.push(newTx);
  saveData();

  closeModal('stockOutModal');
  document.getElementById('stockOutForm').reset();
  showToast(`تم تسجيل إذن الصرف بنجاح (-${pieces.toLocaleString()} قطعة لقصة ${cutNumber})`, 'success');

  renderDashboard();
  renderExcelSheet();
  renderCutsView();
}

// ==========================================
// 11. CUTS & TRANSACTIONS CRUD
// ==========================================

function handleAddCutSubmit(e) {
  e.preventDefault();

  const originalNumber = document.getElementById('editCutOriginalNumber').value;
  const cutNumber = document.getElementById('cutNumberInput').value.trim().toUpperCase();
  const modelName = document.getElementById('cutModelInput').value.trim();
  const fabricType = document.getElementById('cutFabricInput').value.trim();
  const color = document.getElementById('cutColorInput').value.trim();
  const season = document.getElementById('cutSeasonInput').value.trim();
  const targetPieces = Number(document.getElementById('cutTargetPiecesInput').value) || 0;
  const notes = document.getElementById('cutNotesInput').value.trim();

  if (!cutNumber || !modelName) {
    showToast('يرجى إدخال كود القصة واسم الموديل', 'error');
    return;
  }

  if (originalNumber) {
    // Edit existing cut
    const cut = appState.cuts.find(c => c.cutNumber === originalNumber);
    if (cut) {
      cut.modelName = modelName;
      cut.fabricType = fabricType;
      cut.color = color;
      cut.season = season;
      cut.targetPieces = targetPieces;
      cut.notes = notes;
    }
    showToast(`تم حفظ تعديل بيانات القصة ${originalNumber}`, 'success');
  } else {
    // Add new cut
    if (appState.cuts.some(c => c.cutNumber === cutNumber)) {
      showToast('كود هذه القصة مسجل بالفعل مسبقاً!', 'error');
      return;
    }
    appState.cuts.push({
      cutNumber,
      modelName,
      fabricType,
      color,
      season: season || 'صيف 2025',
      targetPieces,
      notes,
      createdAt: new Date().toISOString().split('T')[0]
    });
    showToast(`تمت إضافة كود القصة ${cutNumber} بنجاح`, 'success');
  }

  saveData();
  closeModal('addCutModal');
  document.getElementById('addCutForm').reset();
  renderCutsView();
}

function openEditCutModal(cutNumber) {
  const cut = appState.cuts.find(c => c.cutNumber === cutNumber);
  if (!cut) return;

  document.getElementById('editCutOriginalNumber').value = cut.cutNumber;
  document.getElementById('cutNumberInput').value = cut.cutNumber;
  document.getElementById('cutNumberInput').disabled = true;
  document.getElementById('cutModelInput').value = cut.modelName;
  document.getElementById('cutFabricInput').value = cut.fabricType || '';
  document.getElementById('cutColorInput').value = cut.color || '';
  document.getElementById('cutSeasonInput').value = cut.season || '';
  document.getElementById('cutTargetPiecesInput').value = cut.targetPieces || 0;
  document.getElementById('cutNotesInput').value = cut.notes || '';
  document.getElementById('cutModalTitle').textContent = `تعديل بيانات القصة ${cut.cutNumber}`;

  openModal('addCutModal');
}

function deleteCut(cutNumber) {
  if (confirm(`هل أنت متأكد من رغبتك في حذف القصة (${cutNumber}) وجميع الحركات المرتبطة بها؟`)) {
    appState.cuts = appState.cuts.filter(c => c.cutNumber !== cutNumber);
    appState.transactions = appState.transactions.filter(t => t.cutNumber !== cutNumber);
    saveData();
    showToast(`تم حذف القصة ${cutNumber} بنجاح`, 'info');
    renderCutsView();
    renderDashboard();
    renderExcelSheet();
  }
}

function deleteTransaction(txId) {
  if (confirm('هل أنت متأكد من حذف هذه الحركة من سجل المخزن؟')) {
    appState.transactions = appState.transactions.filter(t => t.id !== txId);
    saveData();
    showToast('تم حذف الحركة وإعادة احتساب الأرصدة فوراً', 'info');
    renderDashboard();
    renderExcelSheet();
    renderTransactionsView();
    renderCutsView();
  }
}

// ==========================================
// 12. EXCEL & CSV EXPORT / IMPORT & AUDIT
// ==========================================

function exportWarehouseExcel() {
  let csv = "\uFEFF"; // UTF-8 BOM for Microsoft Excel Arabic support
  csv += "رقم القصة (A),اسم الموديل (B),نوع الحركة (C),المرحلة الإنتاجية (D),عدد القطع (E),عدد الأثواب (F),الوزن كجم (G),الجهة والورشة (H),المسؤول (I),التاريخ (J),رقم المستند (K),الملاحظات (L)\r\n";

  appState.transactions.forEach(t => {
    csv += `"${t.cutNumber || ''}","${t.modelName || ''}","${t.type || ''}","${t.stage || ''}",${t.pieces || 0},${t.rolls || 0},${t.weightKg || 0},"${t.party || ''}","${t.responsible || ''}","${t.date || ''}","${t.docNumber || ''}","${t.notes || ''}"\r\n`;
  });

  const dateStr = new Date().toISOString().split('T')[0];
  downloadFile(csv, `مخازن_القصات_إكسل_${dateStr}.csv`, 'text/csv;charset=utf-8;');
  showToast('تم تصدير شيت المخزن إلى Excel بترميز UTF-8 سليم 100%', 'success');
}

// Handle File Input Change for Excel / CSV Import
document.addEventListener('DOMContentLoaded', () => {
  const importInput = document.getElementById('excelImportFileInput');
  if (importInput) {
    importInput.addEventListener('change', handleExcelFileSelect);
  }

  const jsonInput = document.getElementById('jsonRestoreFileInput');
  if (jsonInput) {
    jsonInput.addEventListener('change', handleJsonRestoreSelect);
  }
});

function handleExcelFileSelect(e) {
  const file = e.target.files[0];
  if (!file) return;

  const reader = new FileReader();
  reader.onload = function(event) {
    const text = event.target.result;
    parseAndPreviewExcelImport(text);
  };
  reader.readAsText(file);
}

function parseAndPreviewExcelImport(csvText) {
  const lines = csvText.split(/\r?\n/).filter(line => line.trim() !== '');
  if (lines.length < 2) {
    showToast('الملف فارغ أو لا يحتوي على صفوف صالحة', 'error');
    return;
  }

  // Skip header line
  const rows = [];
  let validCount = 0;
  let invalidCount = 0;

  for (let i = 1; i < lines.length; i++) {
    const line = lines[i];
    // Split by comma ignoring commas inside quotes
    const parts = line.split(/,(?=(?:(?:[^"]*"){2})*[^"]*$)/).map(p => p.replace(/^"|"$/g, '').trim());

    if (parts.length < 3 || !parts[0] || !parts[1]) {
      invalidCount++;
      rows.push({
        rawIndex: i,
        cutNumber: parts[0] || 'غير صالح',
        modelName: parts[1] || 'غير صالح',
        type: parts[2] || '-',
        pieces: 0,
        party: parts[7] || '-',
        isValid: false,
        reason: 'أعمدة ناقصة أو غير صالحة'
      });
      continue;
    }

    const cutNumber = parts[0].toUpperCase();
    const modelName = parts[1];
    let type = parts[2] || 'وارد';
    if (type.includes('صرف') || type.includes('out') || type.includes('-')) type = 'صرف';
    else type = 'وارد';

    const stage = parts[3] || (type === 'وارد' ? 'قص' : 'خياطة');
    const pieces = Math.abs(parseInt(parts[4])) || 0;
    const rolls = parseInt(parts[5]) || 0;
    const weightKg = parseFloat(parts[6]) || 0;
    const party = parts[7] || (type === 'وارد' ? 'المقصدار' : 'الورشة');
    const responsible = parts[8] || 'أمين المخزن';
    const date = parts[9] || new Date().toISOString().split('T')[0];
    const docNumber = parts[10] || `IMP-${Date.now().toString().slice(-4)}`;
    const notes = parts[11] || 'مستورد من إكسل';

    if (pieces <= 0) {
      invalidCount++;
      rows.push({
        rawIndex: i,
        cutNumber,
        modelName,
        type,
        pieces,
        party,
        isValid: false,
        reason: 'الكمية يجب أن تكون أكبر من صفر'
      });
    } else {
      validCount++;
      rows.push({
        rawIndex: i,
        cutNumber,
        modelName,
        type,
        stage,
        pieces,
        rolls,
        weightKg,
        party,
        responsible,
        date,
        docNumber,
        notes,
        isValid: true
      });
    }
  }

  appState.pendingImportRows = rows.filter(r => r.isValid);

  document.getElementById('importTotalRowsCount').textContent = lines.length - 1;
  document.getElementById('importValidRowsCount').textContent = validCount;
  document.getElementById('importInvalidRowsCount').textContent = invalidCount;

  const tbody = document.getElementById('importPreviewTableBody');
  tbody.innerHTML = rows.map(r => `
    <tr class="${r.isValid ? 'bg-white' : 'bg-red-50 text-red-700 font-semibold'}">
      <td class="p-2 border-b">${r.rawIndex}</td>
      <td class="p-2 border-b font-bold">${r.cutNumber}</td>
      <td class="p-2 border-b">${r.modelName}</td>
      <td class="p-2 border-b text-center font-bold">${r.type}</td>
      <td class="p-2 border-b text-center font-bold">${r.pieces}</td>
      <td class="p-2 border-b">${r.party}</td>
      <td class="p-2 border-b">
        ${r.isValid ? '<span class="text-green-700 font-bold"><i class="fa-solid fa-circle-check"></i> جاهز</span>' : `<span class="text-red-600">${r.reason}</span>`}
      </td>
    </tr>
  `).join('');

  openModal('importAuditModal');
}

function commitExcelImport() {
  if (!appState.pendingImportRows || appState.pendingImportRows.length === 0) {
    showToast('لا توجد صفوف صالحة للاستيراد', 'error');
    return;
  }

  let importedCount = 0;

  appState.pendingImportRows.forEach(row => {
    // Check or add cut
    let cut = appState.cuts.find(c => c.cutNumber === row.cutNumber);
    if (!cut) {
      appState.cuts.push({
        cutNumber: row.cutNumber,
        modelName: row.modelName,
        fabricType: 'مستورد',
        color: 'مستورد',
        season: 'صيف 2025',
        targetPieces: row.pieces,
        notes: 'تم إنشاؤها عبر الاستيراد',
        createdAt: row.date
      });
    }

    // Add transaction
    appState.transactions.push({
      id: `TX-IMP-${Date.now()}-${Math.random().toString(36).substr(2, 4)}`,
      cutNumber: row.cutNumber,
      modelName: row.modelName,
      type: row.type,
      stage: row.stage,
      pieces: row.pieces,
      rolls: row.rolls,
      weightKg: row.weightKg,
      party: row.party,
      responsible: row.responsible,
      date: row.date,
      docNumber: row.docNumber,
      notes: row.notes
    });

    importedCount++;
  });

  saveData();
  closeModal('importAuditModal');
  showToast(`تم استيراد ${importedCount} حركة بنجاح إلى قاعدة البيانات`, 'success');

  renderDashboard();
  renderExcelSheet();
  renderCutsView();
}

function exportDatabaseJson() {
  const jsonStr = JSON.stringify({
    timestamp: new Date().toISOString(),
    cuts: appState.cuts,
    transactions: appState.transactions,
    audits: appState.audits
  }, null, 2);

  downloadFile(jsonStr, `نسخة_احتياطية_مخزن_القصات_${new Date().toISOString().split('T')[0]}.json`, 'application/json');
  showToast('تم تنزيل النسخة الاحتياطية بنجاح', 'success');
}

function handleJsonRestoreSelect(e) {
  const file = e.target.files[0];
  if (!file) return;

  const reader = new FileReader();
  reader.onload = function(event) {
    try {
      const data = JSON.parse(event.target.result);
      if (data.cuts && data.transactions) {
        if (confirm('هل أنت متأكد من رغبتك في استعادة هذه النسخة الاحتياطية؟ سيتم دمج أو استبدال البيانات الحالية.')) {
          appState.cuts = data.cuts;
          appState.transactions = data.transactions;
          appState.audits = data.audits || [];
          saveData();
          showToast('تمت استعادة النسخة الاحتياطية بنجاح 100%', 'success');
          renderDashboard();
          renderExcelSheet();
        }
      } else {
        showToast('الملف لا يحتوي على هيكل بيانات صالح', 'error');
      }
    } catch (err) {
      showToast('خطأ في قراءة ملف JSON', 'error');
    }
  };
  reader.readAsText(file);
}

function confirmResetDemoData() {
  if (confirm('هل أنت متأكد من رغبتك في إعادة تعيين البيانات إلى البيانات النموذجية الأصلية؟')) {
    appState.cuts = [...DEFAULT_DATA.cuts];
    appState.transactions = [...DEFAULT_DATA.transactions];
    appState.audits = [];
    saveData();
    showToast('تمت استعادة البيانات النموذجية للمخزن بنجاح', 'info');
    renderDashboard();
    renderExcelSheet();
    renderCutsView();
  }
}

// ==========================================
// 13. UTILITIES & MODAL HELPERS
// ==========================================

function openModal(modalId) {
  const modal = document.getElementById(modalId);
  if (modal) {
    modal.classList.remove('hidden');
    // Pre-populate date inputs if empty
    modal.querySelectorAll('input[type="date"]').forEach(inp => {
      if (!inp.value) inp.value = new Date().toISOString().split('T')[0];
    });

    if (modalId === 'stockOutModal') {
      populateCutsDatalists();
      handleOutCutSelected();
    }
  }
}

function closeModal(modalId) {
  const modal = document.getElementById(modalId);
  if (modal) {
    modal.classList.add('hidden');
  }
}

function showToast(message, type = 'info') {
  const container = document.getElementById('toastContainer');
  if (!container) return;

  const toast = document.createElement('div');
  const colors = {
    success: 'bg-green-700 text-white',
    error: 'bg-red-700 text-white',
    info: 'bg-gray-800 text-white'
  };

  const icons = {
    success: '<i class="fa-solid fa-circle-check"></i>',
    error: '<i class="fa-solid fa-circle-exclamation"></i>',
    info: '<i class="fa-solid fa-info-circle"></i>'
  };

  toast.className = `toast-item pointer-events-auto px-4 py-2.5 rounded-xl shadow-lg flex items-center gap-2 text-xs font-bold ${colors[type] || colors.info}`;
  toast.innerHTML = `${icons[type] || icons.info} <span>${message}</span>`;

  container.appendChild(toast);

  setTimeout(() => {
    toast.style.opacity = '0';
    toast.style.transition = 'opacity 0.3s ease';
    setTimeout(() => toast.remove(), 300);
  }, 3500);
}

function downloadFile(content, fileName, mimeType) {
  const blob = new Blob([content], { type: mimeType });
  const url = URL.createObjectURL(blob);
  const a = document.createElement('a');
  a.href = url;
  a.download = fileName;
  document.body.appendChild(a);
  a.click();
  document.body.removeChild(a);
  URL.revokeObjectURL(url);
}

// Mobile Sidebar Controls
function openMobileSidebar() {
  const sidebar = document.getElementById('sidebar');
  const backdrop = document.getElementById('sidebarBackdrop');
  if (sidebar && backdrop) {
    sidebar.classList.remove('translate-x-full');
    backdrop.classList.remove('hidden');
  }
}

function closeMobileSidebar() {
  const sidebar = document.getElementById('sidebar');
  const backdrop = document.getElementById('sidebarBackdrop');
  if (sidebar && backdrop) {
    sidebar.classList.add('translate-x-full');
    backdrop.classList.add('hidden');
  }
}

// ==========================================
// 14. APP BOOTSTRAP
// ==========================================

window.addEventListener('DOMContentLoaded', () => {
  // Set live date
  const dateEl = document.getElementById('headerLiveDate');
  if (dateEl) {
    const opts = { weekday: 'long', year: 'numeric', month: 'long', day: 'numeric' };
    dateEl.textContent = new Date().toLocaleDateString('ar-EG', opts);
  }

  // Bind mobile menu events
  document.getElementById('mobileMenuBtn')?.addEventListener('click', openMobileSidebar);
  document.getElementById('closeMobileMenuBtn')?.addEventListener('click', closeMobileSidebar);
  document.getElementById('sidebarBackdrop')?.addEventListener('click', closeMobileSidebar);

  // Close modals on clicking backdrop
  document.querySelectorAll('.modal-overlay').forEach(overlay => {
    overlay.addEventListener('click', (e) => {
      if (e.target === overlay) {
        overlay.classList.add('hidden');
      }
    });
  });

  // Init Data & Render View
  initDatabase();
  renderDashboard();
  populateCutsDatalists();
});
