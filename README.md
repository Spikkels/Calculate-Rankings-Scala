# Calculate Rankings

Calculates the results of a league.

The team results are sorted from the top (Winners) to the bottom (Losers)
Teams with the same league score are given the same Rank

Example:
```txt
1. Tarantulas: 6 pts
2. Lions: 5 pts
3. FC Awesome: 1 pt
3. Snakes: 1 pt
5. Grouches: 0 pts
```

Game results can be imported from a file.
Results are displayed if the import was successfully done.

If there are any errors then the errors will be displayed of every line that has a error.

The program will stop running if there was result output or errors.

Here is an example of a file to import:

```txt
Lions 3, Snakes 3
Tarantulas 1, FC Awesome 0
Lions 1, FC Awesome 1
Tarantulas 3, Snakes 1
Lions 4, Grouches 0
```

#### FORMAT RULES
- Results are not imported if a line contains the incorrect formats
- Teams cannot be the same
- No empty lines are allowed
- Team names can contain more than one word


## Usage Requirements
```txt
- Openjdk 19
- scala 2.13.10 
- sbt 1.8.2
```

### Run program
Navigate project directory with the build.sbt file:
```python
sbt run
```
### Run tests
Navigate project directory with the build.sbt file:

```terminal
sbt test
```

## License

[MIT](https://choosealicense.com/licenses/mit/)