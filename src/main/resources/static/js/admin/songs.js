// $(document).ready(function () {

    getSongsTable();

    function getSongsTable() {
        $.ajax({
            type: 'GET',
            url: "http://localhost:8080/api/admin/all_songs",

            contentType: 'application/json;',
            headers: {
                'Accept': 'application/json',
                'Content-Type': 'application/json'
            },
            async: true,
            cache: false,
            dataType: 'JSON',
            success: function (listSong) {
                var htmlTable = "";

                for (var i = 0; i < listSong.length; i++) {

                    htmlTable += ('<tr id="listSongs">');
                    htmlTable += ('<td id="tableSongId">' + listSong[i].id + '</td>');
                    htmlTable += ('<td id="tableSongName">' + listSong[i].name + '</td>');
                    htmlTable += ('<td id="tableSongAuthor">' + listSong[i].author.name + '</td>');
                    htmlTable += ('<td id="tableSongGenre">' + listSong[i].genre.name + '</td>');
                    htmlTable += ('<td><a id="editSongBtn' + listSong[i].id  + '" onclick="editSong(' + listSong[i].id + ')" class="btn btn-sm btn-info" role="button" data-toggle="modal"' +
                        ' data-target="#editSong">Изменить</a></td>');
                    htmlTable += ('<td><button id="deleteSongBtn" class="btn btn-sm btn-info" type="button">Удалить</button></td>');
                    htmlTable += ('</tr>');
                }

                $("#songsTable #list").remove();
                $("#getSongsTable").after(htmlTable);
            }
        });
    }

    //edit song GET
    function editSong(id) {

        //полючаю песню по id
        $.ajax({
            url: 'http://localhost:8080/api/admin/song/' + id,
            method: 'GET',
            success: function (editData) {

                //заполняю модалку
                $("#updateSongId").val(editData.id);
                $("#updateSongName").val(editData.name);
                $("#updateSongAuthor").val(editData.author.name);
                $("#updateSongGenre").val(editData.genre.name);

                // var authorObj = editData.author;

                //получаем жанр песни и список жанров из БД на выбор
                getAllGenre(editData.genre.name);

            },
            error: function (error) {
                alert("err: " + error);
            }
        });
    }

    //получаем жанр песни и список жанров из БД на выбор
    function getAllGenre(genreName) {

        //очищаю option в модалке
        $('#updateSongGenre').empty();

        var genreRow = '';
        $.getJSON("http://localhost:8080/api/admin/all_genre", function (data) {
            $.each(data, function (key, value) {
                genreRow += '<option id="' + value.id + '" ';

                //если жанр из таблицы песен совпадает с жанром из БД - устанавлваем в selected
                if (genreName == value.name) {
                    genreRow += 'selected';
                }
                genreRow += ' value="' + value.name + '">' + value.name + '</option>';
            });
            $('#updateSongGenre').append(genreRow);
        });
    }

    //ОСТАНОВИЛСЯ ЗДЕСЬ!
    //edit song PUT
    $("#updateSongBtn").click(function (event) {
        event.preventDefault();
        updateSongForm();
    });

    function updateSongForm() {

        var editSong = {};
        $.ajax({
            url: 'http://localhost:8080/api/admin/song/' + $("#updateSongId").val(),
            method: 'GET',
            success: function (editData) {
                editSong = editData;
                alert("editSong=editData " + editSong);
            },
            error: function (error) {
                alert("err: " + error);
            }
        });

        editSong.id = $("#updateSongId").val();
        editSong.name = $("#updateSongName").val();
        editSong.author.name = $("#updateSongAuthor").val();
        editSong.genre = $("#updateSongGenre option:selected").val();



        alert("editSong before put " + JSON.stringify(editSong));

        $.ajax({
            type: 'PUT',
            url: 'http://localhost:8080/api/admin/update_song',
            contentType: 'application/json',
            data: JSON.stringify(editSong),
            headers: {
                'Accept': 'application/json',
                'Content-Type': 'application/json'
            },
            async: true,
            cache: false,
            dataType: 'JSON',
        });
        location.reload();
    }

    //delete song
    $(document).on('click', '#deleteSongBtn', function () {
        var id = $(this).closest('tr').find('#tableSongId').text();
        deleteSong(id);
    });

    function deleteSong(id) {
        $.ajax({
            type: 'delete',
            url: 'http://localhost:8080/api/admin/delete_song',
            contentType: 'application/json',
            data: JSON.stringify(id),
            headers: {
                'Accept': 'application/json',
                'Content-Type': 'application/json'
            },
            async: false,
            cache: false,
            dataType: 'JSON'
        });
        location.reload();
    }

    // function getSelectedGenre() {
    //     var genre = {};
    //     var selectedGenre = $("#updateSongGenre option:selected").val();
    //     genre.id = selectedGenre.id;
    //     genre.name = selectedGenre.name;
    //     return genre;
    // }
// });