# [Katalon Studio] Flattening JSON by Node via command line

This is a small Katalon Studio project for demonstration purpose. You can download the zip from the [Releases](https://github.com/kazurayam/FlattenJsonByNodeFlatInKatalonStudio/releases/) page, unzip it, open it with your local Katalon Studio. This project was developed using Katalon Studio v8.3.0 but is not version-dependent. It should work on any versions.

This project was developed to propose a solution to a question posted in the Katalon Community:

- https://forum.katalon.com/t/convert-json-to-dot-notation/68758/3


## Problem to solve

The original post asked how to convert something like this:

```
String jsonString = ‘’'{“menu”: {
“id”: “file”,
“tools”: {
“actions”: [
{“id”: “new”, “title”: “New File”},
{“id”: “open”, “title”: “Open File”},
{“id”: “close”, “title”: “Close File”}
],
“errors”:[ ]
```

into something like this:

```
menu.id.file
menu.tools.actions.id.“new”.title.“New File”
menu.tools.actions.id.“title”.“Open File”
menu.tools.actionjs.id.“close”.title.“Close File”
menu.errors.[ ]
```

He wants to automate the following process in Katalon Studio.

1. get a JSON from a remote Web Service URL
2. convert the JSON response into the format he wants (he called it *dot notation*)

## Solution

As far as I see, Java/Groovy language is not very good at JSON processing.
All of us know *JSON* stands for "JavaScript Object Notation".
Therefore JSON is best supported by JavaScript language.

I found a package for Node.js named **flat**, which provides a way to *flatten* a JSON.

- https://github.com/hughsk/flat

How to utilize *flat* in Katalon Studio?

I will develop a test case (Groovy script in Katalon Studio) that does the following:

1. The TC makes a GET request to a URL, obtain a JSON as response
2. The TC saves the JSON text into a local file
3. The TC execute a subprocess that runs Node in the OS commandline.
4.

### Prerequisites

1. You need Node.js installed into your machine. See [the doc "How to install Node.js"](https://nodejs.dev/en/learn/how-to-install-nodejs) for how to install Node.
2. You need to install the "flat" package into your Node installation. See [the README of flat](https://github.com/hughsk/flat) for how to.
3. In the `<projectDir>/Drivers` folder, I included "subprocessj" library. If you miss the jar, please download the jar from [the Release of subprocessj](https://github.com/kazurayam/subprocessj/releases).

The "subprocessj" library is a thin wrapper of [`java.lang.ProcessBuilder`](https://docs.oracle.com/javase/8/docs/api/java/lang/ProcessBuilder.html) to make it simpler to use for me. See the [doc of Subprocessj](https://kazurayam.github.io/subprocessj) for detail.

## Description

In your Katalon Stdio, just open [Test Cases/TC3](Scripts/TC3/Script1661069368294.groovy) and run it.

TC3 will do the following:

1. GET a URL https://ghibliapi.herokuapp.com/films which returns a JSON, which contains a list of films created by Studio Ghibli.
2. write the JSON into a local file.
3. execute a JavaScript script [`Include/Scripts/node/flattenJsonResponse.js`](Include/scripts/node/flattenJsonResponse.js) with Node.js while specifying the JSON file as param.
4. the `flattenJsonResponse.js` scripts converts the original JSON file into another *"flattened"* JSON text and write it into stdout of the subprocess where "node" command runs.
5. TC3 fetches the stdout of the subprocess to obtain the flattened JSON text. TC3 pretty-prints the JSON into the console.


### Downloaded JSON

https://ghibliapi.herokuapp.com/films gives a JSON like this:

```
[
  {
    "id": "2baf70d1-42bb-4437-b551-e5fed5a87abe",
    "title": "Castle in the Sky",
    "original_title": "天空の城ラピュタ",
    "original_title_romanised": "Tenkū no shiro Rapyuta",
    "image": "https://image.tmdb.org/t/p/w600_and_h900_bestv2/npOnzAbLh6VOIu3naU5QaEcTepo.jpg",
    "movie_banner": "https://image.tmdb.org/t/p/w533_and_h300_bestv2/3cyjYtLWCBE1uvWINHFsFnE8LUK.jpg",
    "description": "The orphan Sheeta inherited a mysterious crystal that links her to the mythical sky-kingdom of Laputa. With the help of resourceful Pazu and a rollicking band of sky pirates, she makes her way to the ruins of the once-great civilization. Sheeta and Pazu must outwit the evil Muska, who plans to use Laputa's science to make himself ruler of the world.",
    "director": "Hayao Miyazaki",
    "producer": "Isao Takahata",
    "release_date": "1986",
    "running_time": "124",
    "rt_score": "95",
    "people": [
      "https://ghibliapi.herokuapp.com/people/598f7048-74ff-41e0-92ef-87dc1ad980a9",
      "https://ghibliapi.herokuapp.com/people/fe93adf2-2f3a-4ec4-9f68-5422f1b87c01",
      "https://ghibliapi.herokuapp.com/people/3bc0b41e-3569-4d20-ae73-2da329bf0786",
      "https://ghibliapi.herokuapp.com/people/40c005ce-3725-4f15-8409-3e1b1b14b583",
      "https://ghibliapi.herokuapp.com/people/5c83c12a-62d5-4e92-8672-33ac76ae1fa0",
      "https://ghibliapi.herokuapp.com/people/e08880d0-6938-44f3-b179-81947e7873fc",
      "https://ghibliapi.herokuapp.com/people/2a1dad70-802a-459d-8cc2-4ebd8821248b"
    ],
    "species": [
      "https://ghibliapi.herokuapp.com/species/af3910a6-429f-4c74-9ad5-dfe1c4aa04f2"
    ],
    "locations": [
      "https://ghibliapi.herokuapp.com/locations/"
    ],
    "vehicles": [
      "https://ghibliapi.herokuapp.com/vehicles/4e09b023-f650-4747-9ab9-eacf14540cfb"
    ],
    "url": "https://ghibliapi.herokuapp.com/films/2baf70d1-42bb-4437-b551-e5fed5a87abe"
  },
  ... (CONTINUE)
```

### The result

TC3 emits the result like this:

```
{
    "0.id": "2baf70d1-42bb-4437-b551-e5fed5a87abe",
    "0.title": "Castle in the Sky",
    "0.original_title": "\u5929\u7a7a\u306e\u57ce\u30e9\u30d4\u30e5\u30bf",
    "0.original_title_romanised": "Tenk\u016b no shiro Rapyuta",
    "0.image": "https://image.tmdb.org/t/p/w600_and_h900_bestv2/npOnzAbLh6VOIu3naU5QaEcTepo.jpg",
    "0.movie_banner": "https://image.tmdb.org/t/p/w533_and_h300_bestv2/3cyjYtLWCBE1uvWINHFsFnE8LUK.jpg",
    "0.description": "The orphan Sheeta inherited a mysterious crystal that links her to the mythical sky-kingdom of Laputa. With the help of resourceful Pazu and a rollicking band of sky pirates, she makes her way to the ruins of the once-great civilization. Sheeta and Pazu must outwit the evil Muska, who plans to use Laputa's science to make himself ruler of the world.",
    "0.director": "Hayao Miyazaki",
    "0.producer": "Isao Takahata",
    "0.release_date": "1986",
    "0.running_time": "124",
    "0.rt_score": "95",
    "0.people.0": "https://ghibliapi.herokuapp.com/people/598f7048-74ff-41e0-92ef-87dc1ad980a9",
    "0.people.1": "https://ghibliapi.herokuapp.com/people/fe93adf2-2f3a-4ec4-9f68-5422f1b87c01",
    "0.people.2": "https://ghibliapi.herokuapp.com/people/3bc0b41e-3569-4d20-ae73-2da329bf0786",
    "0.people.3": "https://ghibliapi.herokuapp.com/people/40c005ce-3725-4f15-8409-3e1b1b14b583",
    "0.people.4": "https://ghibliapi.herokuapp.com/people/5c83c12a-62d5-4e92-8672-33ac76ae1fa0",
    "0.people.5": "https://ghibliapi.herokuapp.com/people/e08880d0-6938-44f3-b179-81947e7873fc",
    "0.people.6": "https://ghibliapi.herokuapp.com/people/2a1dad70-802a-459d-8cc2-4ebd8821248b",
    "0.species.0": "https://ghibliapi.herokuapp.com/species/af3910a6-429f-4c74-9ad5-dfe1c4aa04f2",
    "0.locations.0": "https://ghibliapi.herokuapp.com/locations/",
    "0.vehicles.0": "https://ghibliapi.herokuapp.com/vehicles/4e09b023-f650-4747-9ab9-eacf14540cfb",
    "0.url": "https://ghibliapi.herokuapp.com/films/2baf70d1-42bb-4437-b551-e5fed5a87abe",
    "1.id": "12cfb892-aac0-4c5b-94af-521852e46d6a",
    "1.title": "Grave of the Fireflies",
    "1.original_title": "\u706b\u5782\u308b\u306e\u5893",
    "1.original_title_romanised": "Hotaru no haka",
    "1.image": "https://image.tmdb.org/t/p/w600_and_h900_bestv2/qG3RYlIVpTYclR9TYIsy8p7m7AT.jpg",
    "1.movie_banner": "https://image.tmdb.org/t/p/original/vkZSd0Lp8iCVBGpFH9L7LzLusjS.jpg",
    "1.description": "In the latter part of World War II, a boy and his sister, orphaned when their mother is killed in the firebombing of Tokyo, are left to survive on their own in what remains of civilian life in Japan. The plot follows this boy and his sister as they do their best to survive in the Japanese countryside, battling hunger, prejudice, and pride in their own quiet, personal battle.",
    "1.director": "Isao Takahata",
    "1.producer": "Toru Hara",
    "1.release_date": "1988",
    "1.running_time": "89",
    "1.rt_score": "97",
    "1.people.0": "https://ghibliapi.herokuapp.com/people/",
    "1.species.0": "https://ghibliapi.herokuapp.com/species/af3910a6-429f-4c74-9ad5-dfe1c4aa04f2",
    "1.locations.0": "https://ghibliapi.herokuapp.com/locations/",
    "1.vehicles.0": "https://ghibliapi.herokuapp.com/vehicles/",
    "1.url": "https://ghibliapi.herokuapp.com/films/12cfb892-aac0-4c5b-94af-521852e46d6a",
    "2.id": "58611129-2dbc-4a81-a72f-77ddfc1b1b49",
    "2.title": "My Neighbor Totoro",
    "2.original_title": "\u3068\u306a\u308a\u306e\u30c8\u30c8\u30ed",
    "2.original_title_romanised": "Tonari no Totoro",
    "2.image": "https://image.tmdb.org/t/p/w600_and_h900_bestv2/rtGDOeG9LzoerkDGZF9dnVeLppL.jpg",
    "2.movie_banner": "https://image.tmdb.org/t/p/original/etqr6fOOCXQOgwrQXaKwenTSuzx.jpg",
    "2.description": "Two sisters move to the country with their father in order to be closer to their hospitalized mother, and discover the surrounding trees are inhabited by Totoros, magical spirits of the forest. When the youngest runs away from home, the older sister seeks help from the spirits to find her.",
    "2.director": "Hayao Miyazaki",
    "2.producer": "Hayao Miyazaki",
    "2.release_date": "1988",
    "2.running_time": "86",
    "2.rt_score": "93",
    "2.people.0": "https://ghibliapi.herokuapp.com/people/986faac6-67e3-4fb8-a9ee-bad077c2e7fe",
    "2.people.1": "https://ghibliapi.herokuapp.com/people/d5df3c04-f355-4038-833c-83bd3502b6b9",
    "2.people.2": "https://ghibliapi.herokuapp.com/people/3031caa8-eb1a-41c6-ab93-dd091b541e11",
    "2.people.3": "https://ghibliapi.herokuapp.com/people/87b68b97-3774-495b-bf80-495a5f3e672d",
    "2.people.4": "https://ghibliapi.herokuapp.com/people/d39deecb-2bd0-4770-8b45-485f26e1381f",
    "2.people.5": "https://ghibliapi.herokuapp.com/people/591524bc-04fe-4e60-8d61-2425e42ffb2a",
    "2.people.6": "https://ghibliapi.herokuapp.com/people/c491755a-407d-4d6e-b58a-240ec78b5061",
    "2.people.7": "https://ghibliapi.herokuapp.com/people/f467e18e-3694-409f-bdb3-be891ade1106",
    "2.people.8": "https://ghibliapi.herokuapp.com/people/08ffbce4-7f94-476a-95bc-76d3c3969c19",
    "2.people.9": "https://ghibliapi.herokuapp.com/people/0f8ef701-b4c7-4f15-bd15-368c7fe38d0a",
    "2.species.0": "https://ghibliapi.herokuapp.com/species/af3910a6-429f-4c74-9ad5-dfe1c4aa04f2",
    "2.species.1": "https://ghibliapi.herokuapp.com/species/603428ba-8a86-4b0b-a9f1-65df6abef3d3",
    "2.species.2": "https://ghibliapi.herokuapp.com/species/74b7f547-1577-4430-806c-c358c8b6bcf5",
    "2.locations.0": "https://ghibliapi.herokuapp.com/locations/",
    "2.vehicles.0": "https://ghibliapi.herokuapp.com/vehicles/",
    "2.url": "https://ghibliapi.herokuapp.com/films/58611129-2dbc-4a81-a72f-77ddfc1b1b49",
    "3.id": "ea660b10-85c4-4ae3-8a5f-41cea3648e3e",
    "3.title": "Kiki's Delivery Service",
    ...(CONTINUE)
```
