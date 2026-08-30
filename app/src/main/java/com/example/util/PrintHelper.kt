package com.example.util

import android.content.Context
import android.content.Intent
import android.print.PrintAttributes
import android.print.PrintManager
import android.webkit.WebResourceRequest
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.Toast
import com.example.data.local.entity.ExamScheduleEntity
import com.example.data.local.entity.ExamSeatingEntity
import com.example.data.local.entity.TimetableSlotEntity

object PrintHelper {

    fun printHtml(context: Context, docName: String, htmlContent: String) {
        val webView = WebView(context)
        webView.webViewClient = object : WebViewClient() {
            override fun shouldOverrideUrlLoading(view: WebView?, request: WebResourceRequest?): Boolean = false

            override fun onPageFinished(view: WebView?, url: String?) {
                val printManager = context.getSystemService(Context.PRINT_SERVICE) as? PrintManager
                if (printManager != null) {
                    val printAdapter = webView.createPrintDocumentAdapter(docName)
                    val attributes = PrintAttributes.Builder()
                        .setMediaSize(PrintAttributes.MediaSize.ISO_A4)
                        .setResolution(PrintAttributes.Resolution("id", "print", 300, 300))
                        .setMinMargins(PrintAttributes.Margins.NO_MARGINS)
                        .build()
                    printManager.print(docName, printAdapter, attributes)
                } else {
                    Toast.makeText(context, "Print service not available on this device", Toast.LENGTH_SHORT).show()
                }
            }
        }
        webView.loadDataWithBaseURL(null, htmlContent, "text/html", "UTF-8", null)
    }

    fun shareText(context: Context, title: String, content: String) {
        val intent = Intent(Intent.ACTION_SEND).apply {
            type = "text/plain"
            putExtra(Intent.EXTRA_SUBJECT, title)
            putExtra(Intent.EXTRA_TEXT, content)
        }
        context.startActivity(Intent.createChooser(intent, "Export / Share via"))
    }

    fun generateTimetableHtml(
        title: String,
        subtitle: String,
        slots: List<TimetableSlotEntity>
    ): String {
        val days = listOf(
            1 to "Monday",
            2 to "Tuesday",
            3 to "Wednesday",
            4 to "Thursday",
            5 to "Friday",
            6 to "Saturday"
        )
        val periods = 1..7

        val rowsHtml = StringBuilder()
        for ((dayNum, dayName) in days) {
            val daySlots = slots.filter { it.dayOfWeek == dayNum }
            if (daySlots.isEmpty()) continue

            rowsHtml.append("<tr>")
            rowsHtml.append("<td class='day-col'><strong>$dayName</strong></td>")

            for (p in periods) {
                val slot = daySlots.find { it.periodNumber == p }
                if (slot != null) {
                    rowsHtml.append(
                        """
                        <td class='slot-cell active-slot'>
                            <div class='sub-code'>${slot.subjectCode}</div>
                            <div class='sub-name'>${slot.subjectName}</div>
                            <div class='slot-meta'>${slot.roomNumber} &bull; ${slot.staffName}</div>
                        </td>
                        """.trimIndent()
                    )
                } else {
                    rowsHtml.append("<td class='slot-cell empty-slot'>-</td>")
                }
            }
            rowsHtml.append("</tr>")
        }

        return """
            <!DOCTYPE html>
            <html>
            <head>
                <meta charset="UTF-8">
                <style>
                    body { font-family: 'Segoe UI', Arial, sans-serif; margin: 20px; color: #0F172A; }
                    .header { text-align: center; border-bottom: 3px solid #1E40AF; padding-bottom: 12px; margin-bottom: 18px; }
                    .college-name { font-size: 24px; font-weight: bold; color: #1E40AF; letter-spacing: 1px; }
                    .college-sub { font-size: 13px; color: #475569; margin-top: 2px; }
                    .doc-title { font-size: 18px; font-weight: 600; margin-top: 10px; color: #090D16; }
                    .doc-subtitle { font-size: 14px; color: #2563EB; font-weight: 500; }
                    table { width: 100%; border-collapse: collapse; margin-top: 15px; }
                    th, td { border: 1px solid #CBD5E1; padding: 8px; text-align: center; font-size: 11px; }
                    th { background-color: #1E40AF; color: white; font-weight: 600; text-transform: uppercase; font-size: 11px; }
                    .day-col { background-color: #F1F5F9; font-weight: bold; color: #1E293B; width: 90px; }
                    .slot-cell { vertical-align: top; height: 55px; }
                    .active-slot { background-color: #EFF6FF; }
                    .empty-slot { color: #94A3B8; }
                    .sub-code { font-weight: bold; color: #1E40AF; font-size: 12px; }
                    .sub-name { font-size: 10px; color: #334155; margin: 2px 0; }
                    .slot-meta { font-size: 9px; color: #64748B; background: #DBEAFE; border-radius: 3px; padding: 2px; }
                    .footer { margin-top: 25px; display: flex; justify-content: space-between; font-size: 11px; color: #64748B; border-top: 1px dashed #CBD5E1; padding-top: 10px; }
                </style>
            </head>
            <body>
                <div class="header">
                    <div class="college-name">MDR 1225 TECH – ACADEMIA</div>
                    <div class="college-sub">Affiliated to Academic Council &bull; Autonomous Institution</div>
                    <div class="doc-title">$title</div>
                    <div class="doc-subtitle">$subtitle</div>
                </div>

                <table>
                    <thead>
                        <tr>
                            <th>Day</th>
                            <th>P1<br><small>09:00-09:50</small></th>
                            <th>P2<br><small>09:50-10:40</small></th>
                            <th>P3<br><small>11:00-11:50</small></th>
                            <th>P4<br><small>11:50-12:40</small></th>
                            <th>P5<br><small>01:30-02:20</small></th>
                            <th>P6<br><small>02:20-03:10</small></th>
                            <th>P7<br><small>03:10-04:00</small></th>
                        </tr>
                    </thead>
                    <tbody>
                        $rowsHtml
                    </tbody>
                </table>

                <div class="footer">
                    <div>Generated via MDR 1225 TECH System</div>
                    <div>HOD / Academic Dean Signature: __________________</div>
                </div>
            </body>
            </html>
        """.trimIndent()
    }

    fun generateExamSeatingHtml(
        exam: ExamScheduleEntity,
        seatings: List<ExamSeatingEntity>
    ): String {
        val rowsHtml = StringBuilder()
        for (st in seatings) {
            rowsHtml.append(
                """
                <tr>
                    <td><strong>${st.deskNumber}</strong></td>
                    <td><span class="badge hall">${st.roomNumber}</span></td>
                    <td>R${st.rowNumber} - C${st.colNumber}</td>
                    <td><strong>${st.studentRegNo}</strong></td>
                    <td>${st.studentRollNo}</td>
                    <td style="text-align:left;">${st.studentName}</td>
                    <td>${st.studentDept} (S${st.studentSemester})</td>
                    <td>${st.invigilatorName}</td>
                </tr>
                """.trimIndent()
            )
        }

        return """
            <!DOCTYPE html>
            <html>
            <head>
                <meta charset="UTF-8">
                <style>
                    body { font-family: 'Segoe UI', Arial, sans-serif; margin: 20px; color: #0F172A; }
                    .header { text-align: center; border-bottom: 3px solid #1E40AF; padding-bottom: 10px; margin-bottom: 15px; }
                    .college-name { font-size: 22px; font-weight: bold; color: #1E40AF; }
                    .doc-title { font-size: 16px; font-weight: 600; margin-top: 5px; color: #090D16; }
                    .exam-meta-box { background-color: #F8FAFC; border: 1px solid #E2E8F0; padding: 10px 15px; border-radius: 6px; margin-bottom: 15px; display: flex; justify-content: space-between; font-size: 12px; }
                    table { width: 100%; border-collapse: collapse; margin-top: 10px; }
                    th, td { border: 1px solid #CBD5E1; padding: 6px 8px; text-align: center; font-size: 11px; }
                    th { background-color: #0F172A; color: white; font-weight: 600; text-transform: uppercase; font-size: 10px; }
                    tr:nth-child(even) { background-color: #F8FAFC; }
                    .badge { display: inline-block; padding: 2px 6px; border-radius: 4px; font-weight: bold; font-size: 10px; }
                    .hall { background-color: #DBEAFE; color: #1E40AF; }
                    .footer { margin-top: 25px; display: flex; justify-content: space-between; font-size: 11px; color: #64748B; border-top: 1px solid #CBD5E1; padding-top: 10px; }
                </style>
            </head>
            <body>
                <div class="header">
                    <div class="college-name">MDR 1225 TECH – ACADEMIA</div>
                    <div class="doc-title">SMART EXAM HALL SEATING CHART</div>
                </div>

                <div class="exam-meta-box">
                    <div><strong>Exam:</strong> ${exam.title} (${exam.subjectCode} - ${exam.subjectName})</div>
                    <div><strong>Date:</strong> ${exam.examDate} (${exam.session}: ${exam.startTime} - ${exam.endTime})</div>
                    <div><strong>Total Seated:</strong> ${seatings.size} Students</div>
                </div>

                <table>
                    <thead>
                        <tr>
                            <th>Desk #</th>
                            <th>Hall / Room</th>
                            <th>Grid Position</th>
                            <th>Register No</th>
                            <th>Roll No</th>
                            <th style="text-align:left;">Student Name</th>
                            <th>Dept & Sem</th>
                            <th>Invigilator</th>
                        </tr>
                    </thead>
                    <tbody>
                        $rowsHtml
                    </tbody>
                </table>

                <div class="footer">
                    <div>Controller of Examinations (COE) Office</div>
                    <div>Hall Superintendent Sign: __________________</div>
                </div>
            </body>
            </html>
        """.trimIndent()
    }

    fun generateHallTicketHtml(
        student: ExamSeatingEntity,
        exam: ExamScheduleEntity
    ): String {
        return """
            <!DOCTYPE html>
            <html>
            <head>
                <meta charset="UTF-8">
                <style>
                    body { font-family: 'Segoe UI', Arial, sans-serif; margin: 30px; color: #0F172A; }
                    .ticket-card { border: 2px solid #1E40AF; border-radius: 8px; padding: 24px; max-width: 600px; margin: 0 auto; box-shadow: 0 4px 6px rgba(0,0,0,0.05); }
                    .header { text-align: center; border-bottom: 2px solid #E2E8F0; padding-bottom: 12px; margin-bottom: 18px; }
                    .college-title { font-size: 20px; font-weight: bold; color: #1E40AF; }
                    .ticket-label { font-size: 14px; font-weight: 700; color: #090D16; letter-spacing: 1px; margin-top: 4px; background: #DBEAFE; display: inline-block; padding: 3px 12px; border-radius: 12px; }
                    .grid { display: table; width: 100%; margin-bottom: 16px; }
                    .row { display: table-row; }
                    .cell-label { display: table-cell; font-size: 12px; color: #64748B; padding: 6px 0; width: 35%; }
                    .cell-val { display: table-cell; font-size: 13px; font-weight: 600; color: #0F172A; padding: 6px 0; }
                    .seat-highlight { background: #EFF6FF; border: 1.5px solid #2563EB; border-radius: 6px; padding: 12px; text-align: center; margin: 16px 0; }
                    .seat-title { font-size: 11px; text-transform: uppercase; color: #1E40AF; font-weight: bold; letter-spacing: 0.5px; }
                    .seat-numbers { font-size: 22px; font-weight: 800; color: #1E40AF; margin: 4px 0; }
                    .rules { font-size: 10px; color: #64748B; border-top: 1px dashed #CBD5E1; padding-top: 12px; margin-top: 16px; line-height: 1.4; }
                </style>
            </head>
            <body>
                <div class="ticket-card">
                    <div class="header">
                        <div class="college-title">MDR 1225 TECH – ACADEMIA</div>
                        <div class="ticket-label">OFFICIAL EXAM SEAT ADMIT CARD</div>
                    </div>

                    <div class="grid">
                        <div class="row">
                            <div class="cell-label">Student Name:</div>
                            <div class="cell-val">${student.studentName}</div>
                        </div>
                        <div class="row">
                            <div class="cell-label">Register Number:</div>
                            <div class="cell-val">${student.studentRegNo}</div>
                        </div>
                        <div class="row">
                            <div class="cell-label">Roll Number:</div>
                            <div class="cell-val">${student.studentRollNo}</div>
                        </div>
                        <div class="row">
                            <div class="cell-label">Degree & Branch:</div>
                            <div class="cell-val">B.Tech ${student.studentDept} (Semester ${student.studentSemester}, Sec ${student.studentSection})</div>
                        </div>
                        <div class="row">
                            <div class="cell-label">Subject:</div>
                            <div class="cell-val">${exam.subjectCode} - ${exam.subjectName}</div>
                        </div>
                        <div class="row">
                            <div class="cell-label">Date & Time:</div>
                            <div class="cell-val">${exam.examDate} &bull; ${exam.startTime} - ${exam.endTime} (${exam.session})</div>
                        </div>
                    </div>

                    <div class="seat-highlight">
                        <div class="seat-title">YOUR ALLOCATED EXAM SEAT</div>
                        <div class="seat-numbers">HALL: ${student.roomNumber} &bull; DESK #${student.deskNumber}</div>
                        <div style="font-size: 12px; color: #475569;">Row ${student.rowNumber}, Column ${student.colNumber} &bull; Invigilator: ${student.invigilatorName}</div>
                    </div>

                    <div class="rules">
                        <strong>Instructions for Candidate:</strong><br>
                        1. Candidates must arrive at the examination hall at least 15 minutes before commencement.<br>
                        2. Carry your College ID card along with this Hall Ticket.<br>
                        3. Mobile phones, programmable calculators, and smart devices are strictly prohibited.
                    </div>
                </div>
            </body>
            </html>
        """.trimIndent()
    }
}
