import csv

ifile  = open('SearchResults.csv', "rb")
reader = csv.reader(ifile)

ofile = open('springer.bib', 'w')

rownum = 0
for row in reader:
    # Save header row.
    if rownum > 0:
        ofile.write('@INPROCEEDINGS{')

        # index
        index = '{0}'.format(rownum)
        ofile.write('springer:')
        ofile.write(index)
        ofile.write(',\n')

        # title
        ofile.write('title={')
        ofile.write(row[0])
        ofile.write('},\n')

        # book title
        ofile.write('booktitle={')
        ofile.write(row[1])
        ofile.write('},\n')

        # DOI
        ofile.write('doi={')
        ofile.write(row[5])
        ofile.write('},\n')

        # Author
        ofile.write('author={')
        ofile.write(row[6])
        ofile.write('},\n')

        # Year
        ofile.write('year={')
        ofile.write(row[7])
        ofile.write('},\n')

        # URL
        ofile.write('url={')
        ofile.write(row[8])
        ofile.write('},\n')

        ofile.write('publisher={Springer Berlin Heidelberg},\n')
        ofile.write('}\n')
    rownum += 1

ifile.close()
ofile.close()
