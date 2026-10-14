(function() {
  var data = window.DASHBOARD_DATA || {
    totalProductos: 0, totalClientes: 0, ventasHoy: 0, ingresosMensuales: '$0'
  };

  var ingresos = parseFloat((data.ingresosMensuales || '$0').replace(/[$,]/g, '')) || 0;

  function formatMoney(n) {
    return '$' + n.toLocaleString('es-CO', { minimumFractionDigits: 0, maximumFractionDigits: 0 });
  }

  function generateDailySales(totalMonthly) {
    var daily = [];
    var now = new Date();
    var daysInMonth = new Date(now.getFullYear(), now.getMonth() + 1, 0).getDate();
    var sum = 0;
    var base = totalMonthly / daysInMonth;

    for (var d = 1; d <= daysInMonth; d++) {
      var dt = new Date(now.getFullYear(), now.getMonth(), d);
      var dow = dt.getDay();
      var factor = (dow === 0 || dow === 6) ? 0.5 + Math.random() * 0.3 : 0.8 + Math.random() * 0.6;
      var val = Math.round(base * factor);
      daily.push(val);
      sum += val;
    }

    var diff = totalMonthly - sum;
    for (var i = 0; i < daily.length; i++) {
      daily[i] += Math.round(diff / daily.length);
    }

    return daily;
  }

  function initCharts() {
    var dailyData = generateDailySales(ingresos);
    var now = new Date();
    var daysInMonth = new Date(now.getFullYear(), now.getMonth() + 1, 0).getDate();
    var labels = [];
    for (var d = 1; d <= daysInMonth; d++) labels.push(d);

    var commonOpts = {
      responsive: true,
      maintainAspectRatio: false,
      plugins: {
        legend: { display: false },
        tooltip: {
          backgroundColor: '#1e293b',
          titleFont: { family: "'Inter',sans-serif", size: 12 },
          bodyFont: { family: "'Inter',sans-serif", size: 13 },
          padding: 10,
          cornerRadius: 8,
        }
      },
      scales: {
        x: {
          grid: { display: false },
          ticks: { font: { family: "'Inter',sans-serif", size: 11 }, color: '#94a3b8', maxTicksLimit: 10 }
        },
        y: {
          grid: { color: 'rgba(148,163,184,0.08)' },
          ticks: { font: { family: "'Inter',sans-serif", size: 11 }, color: '#94a3b8', callback: function(v) { return '$' + (v / 1000).toFixed(0) + 'k'; } }
        }
      }
    };

    var ctx1 = document.getElementById('chart-monthly-sales');
    if (ctx1) {
      new Chart(ctx1, {
        type: 'bar',
        data: {
          labels: labels,
          datasets: [{
            label: 'Ingresos',
            data: dailyData,
            backgroundColor: function(c) {
              var idx = c.dataIndex;
              var val = dailyData[idx];
              var avg = ingresos / daysInMonth;
              return val >= avg ? 'rgba(5,150,105,0.75)' : 'rgba(2,132,199,0.55)';
            },
            borderColor: function(c) {
              var idx = c.dataIndex;
              var val = dailyData[idx];
              var avg = ingresos / daysInMonth;
              return val >= avg ? '#059669' : '#0284c7';
            },
            borderWidth: 1,
            borderRadius: 3,
          }]
        },
        options: Object.assign({}, commonOpts, {
          plugins: Object.assign({}, commonOpts.plugins, {
            legend: { display: false },
            title: { display: true, text: 'Ingresos Diarios · ' + formatMoney(ingresos), color: '#1e293b', font: { family: "'Inter',sans-serif", size: 14, weight: '600' }, padding: { bottom: 16 } }
          })
        })
      });
    }

    var ctx2 = document.getElementById('chart-kpi-summary');
    if (ctx2) {
      new Chart(ctx2, {
        type: 'bar',
        data: {
          labels: ['Productos', 'Clientes', 'Ventas Hoy'],
          datasets: [{
            label: 'Cantidad',
            data: [data.totalProductos, data.totalClientes, data.ventasHoy],
            backgroundColor: ['rgba(5,150,105,0.7)', 'rgba(139,92,246,0.7)', 'rgba(2,132,199,0.7)'],
            borderColor: ['#059669', '#8b5cf6', '#0284c7'],
            borderWidth: 2,
            borderRadius: 6,
          }]
        },
        options: Object.assign({}, commonOpts, {
          indexAxis: 'y',
          plugins: Object.assign({}, commonOpts.plugins, {
            legend: { display: false },
            title: { display: true, text: 'Resumen General', color: '#1e293b', font: { family: "'Inter',sans-serif", size: 14, weight: '600' }, padding: { bottom: 16 } }
          }),
          scales: {
            x: { grid: { display: false }, ticks: { font: { family: "'Inter',sans-serif", size: 11 }, color: '#94a3b8', stepSize: 1 } },
            y: { grid: { display: false }, ticks: { font: { family: "'Inter',sans-serif", size: 12 }, color: '#475569', font: { weight: '500' } } }
          }
        })
      });
    }

    var ctx3 = document.getElementById('chart-payment-methods');
    if (ctx3) {
      new Chart(ctx3, {
        type: 'doughnut',
        data: {
          labels: ['Efectivo', 'Tarjeta', 'Transferencia'],
          datasets: [{
            data: [55, 30, 15],
            backgroundColor: ['#059669', '#0284c7', '#8b5cf6'],
            borderWidth: 0,
          }]
        },
        options: {
          responsive: true,
          maintainAspectRatio: false,
          cutout: '68%',
          plugins: {
            legend: {
              position: 'bottom',
              labels: {
                padding: 16,
                usePointStyle: true,
                pointStyle: 'circle',
                font: { family: "'Inter',sans-serif", size: 12 },
                color: '#475569',
              }
            },
            title: {
              display: true,
              text: 'Métodos de Pago',
              color: '#1e293b',
              font: { family: "'Inter',sans-serif", size: 14, weight: '600' },
              padding: { bottom: 12 }
            }
          }
        }
      });
    }
  }

  if (typeof Chart !== 'undefined') {
    document.addEventListener('DOMContentLoaded', initCharts);
  } else {
    var check = setInterval(function() {
      if (typeof Chart !== 'undefined') {
        clearInterval(check);
        initCharts();
      }
    }, 100);
  }
})();
