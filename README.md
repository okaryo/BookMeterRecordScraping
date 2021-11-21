# bookmeter-record-scraping

An application that outputs JSON format reading records from [BookMeter](https://bookmeter.com/)

[読書メーター](https://bookmeter.com/) の読書記録をJSON形式で出力するアプリケーション

## Sample
```json
{
  "recordsCount": 1,
  "records": [
    {
      "date": "2021-11-06",
      "review": "review content",
      "book": {
        "author": {"name": "G パスカル ザカリー"},
        "page": 455,
        "title": "闘うプログラマー［新装版］　ビル・ゲイツの野望を担った男達(Kindle)",
        "url": "https://www.amazon.co.jp/dp/product/B00GSHI04M/ref=as_li_tf_tl?camp=247&creative=1211&creativeASIN=B00GSHI04M&ie=UTF8&linkCode=as2&tag=bookmeter_book_image_image_pc_logoff-22"
      }
    }
  ],
  "totalPages": 455
}
```

## Usage
1. Setup `gradlew`
2. Run following command

`./gradlew run --args="[user id] [output file name]"`

`ex) ./gradlew run --args="739784 main.json"`

3. Then, generate reading records in `./generated/main.json`
