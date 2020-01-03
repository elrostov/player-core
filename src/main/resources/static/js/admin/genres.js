$(document).ready(function () {

    getTable();

    function getTable() {

        $.ajax({
            type: 'GET',
            url: "/api/admin/genre/all_genres",
            contentType: 'application/json;',
            headers: {
                'Accept': 'application/json',
                'Content-Type': 'application/json'
            },
            async: true,
            cache: false,
            dataType: 'JSON',
            success: function (listGenres) {
                $("#genresTable tbody").empty();

                var htmlTable = "";

                for (var i = 0; i < listGenres.length; i++) {
                    htmlTable += ('<tr id="list">');
                    htmlTable += ('<td id="genresId">' + listGenres[i].id + '</td>');
                    htmlTable += ('<td id="genresName">' + listGenres[i].name + '</td>');
                    htmlTable += ('<td><button id="editGenresBtn"  class="btn btn-sm btn-info" type="button" data-toggle="modal"' +
                        ' data-target="#editGenres">изменить</button></td>');
                    htmlTable += ('<td><button id="deleteGenres" class="btn btn-sm btn-info" type="button">удалить</button></td>');
                    htmlTable += ('</tr>');
                }

               /* $("#GenresTable #list").remove();
                $("#getGenresTable").after(htmlTable);*/

                $("#genresTable tbody").append(htmlTable);
                $("#tab-genres-panel").tab('show');
            }

        });
    };

    //addGenre
    $("#addGenreBtn").click(function (event) {
        event.preventDefault();
        addGenre();
    });

    function addGenre() {

        var name = $("#addGenre").val();

        $.ajax({
            type: 'POST',
            url: "/api/admin/genre/add_genre",

            contentType: 'application/json;',
            data: JSON.stringify(name),
            headers: {
                'Accept': 'application/json',
                'Content-Type': 'application/json'
            },
            async: true,
            cache: false,
            dataType: 'JSON',
        });
        getTable();
        $("#addGenre").val("");
    }

    //deleteForm
    $(document).on('click', '#deleteGenres', function () {
        var id = $(this).closest("tr").find("#genresId").text();
        deleteUser(id);
    });

    function deleteUser(id) {
        $.ajax({
            type: 'delete',
            url: "/api/admin/genre/delete_genre",
            contentType: 'application/json;',
            data: JSON.stringify(id),
            headers: {
                'Accept': 'application/json',
                'Content-Type': 'application/json'
            },
            async: false,
            cache: false,
            dataType: 'JSON',
        });
        getTable();
    };


    //updateForm
    $("#editGenresBtn").click(function (event) {
        event.preventDefault();
        updateForm();
    });

    function updateForm() {
        var genre = {
            "id": $("#updateGenresId").val(),
            "name": $("#updateGenresName").val()
        };

        $.ajax({
            type: 'put',
            url: "/api/admin/genre/update_genre",

            contentType: 'application/json;',
            data: JSON.stringify(genre),
            headers: {
                'Accept': 'application/json',
                'Content-Type': 'application/json'
            },
            async: true,
            cache: false,
            dataType: 'JSON',
        });
        getTable();
    };

    //modal form заполнение
    $(document).on('click', '#editGenresBtn', function () {
        $("#updateGenresId").val($(this).closest("tr").find("#genresId").text());
        $("#updateGenresName").val($(this).closest("tr").find("#genresName").text());
    });

});