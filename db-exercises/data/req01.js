/*
Add one document
*/
db.test_01.insert({name:"ys 01",age:22,sum:33});

/*
Find by name
*/
db.test_01.find({name:"ys 01"})

/*
Find by few conditions
*/
db.test_01.find({$or:[
     {age:1},
     {age:3}
     ]})

/*
Update for specific doc attribute value.
Find a document with specific attribute ("age") value and replace an attribute ("name") value with new one
*/
db.test_01.update({age:1},
    {$set:{name:"1 updated"}})

/*
Replace document with new document.
Find a document with specific attribute value and replace this document with new one.
*/
db.test_01.update({name:"ys 01"}, {newDoc:"This is full new document"})

/*
Add one document with sub document
*/
db.test_01.insert({
    name: "ys 02", age: 33, sum: 44,
    favorites: {
        movies: ["Rocky"]
    }
});

/*
Search
*/
db.test_01.find({ "favorites.movies": "Rocky" })

/*
Add (push) item into array
*/
db.test_01.update({name:"ys 02"}, {$push: {"favorites.movies":"Masanya"}});

/*
Add in loop
*/
for(let i = 0; i < 4; i++){
    db.test_01.insert([{name:"nn " + i, age: i, sum: (i * 100)}])
}

/*
Find with range
*/
db.test_01.find({age: {"$lt":4}})