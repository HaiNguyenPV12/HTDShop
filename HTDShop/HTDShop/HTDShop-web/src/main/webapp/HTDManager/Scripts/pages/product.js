$(document).ready(function () {
    alert("Hello");
    // Tạo custom filter ở đây
    $('#tblProduct tfoot th').each(function () {
        var title = $(this).text();
        if (title == "Action") {
            $(this).html('');
        } else if (title == "Status" || title == "Category") {
            $(this).html('<select class="form-control"><option value="">All</option></select>');
        } else {
            $(this).html('<input id="txtSearch' + title + '" type="text" placeholder="Search ' + title + '" class="form-control" style="width:100%" />');
        }

    });
    // Setting table ở đây
    var table = $('#tblProduct').DataTable({
        initComplete: function () {
            var r = $('#tblProduct tfoot tr');
            r.find('th').each(function () {
                $(this).css('padding', 8);
            });
            $('#tblProduct thead').append(r);
        },
        "paging": true,
        "lengthChange": true,
        "lengthMenu": [[5, 10, 25, 50, -1], [5, 10, 25, 50, "All"]],
        "searching": true,
        "ordering": false,
        "info": true,
        "autoWidth": false,
    });

    table.columns().every(function () {
        var thisCol = this;

        $('input', this.footer()).on('keyup change clear', function () {
            if (thisCol.search() !== this.value) {
                thisCol.search(this.value).draw();
            }
        });
        $('select', this.footer()).each(function () {
            var title = thisCol.header();
            /* Đưa dữ liệu custom
            if($(title).html()=="Status"){
                var thisSelect = this;
                thisCol.data().unique().sort().each( function ( d, j ) {
                    $(thisSelect).append("<option value='"+d+"'>"+d+"</option>");
                } );
            }*/
            // Đưa dữ liệu tự động
            var thisSelect = this;
            thisCol.data().unique().sort().each(function (d, j) {
                $(thisSelect).append("<option value='" + d + "'>" + d + "</option>");
            });
        });
        $('select', this.footer()).on('change', function () {
            var val = $.fn.dataTable.util.escapeRegex(
                $(this).val()
            );
            thisCol.search(val ? '^' + val + '$' : '', true, false).draw();
        });
    });
});