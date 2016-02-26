import csv

ifile  = open('SearchResults.csv', "rb")
reader = csv.reader(ifile)

ofile = open('springer.ris', 'w')

rownum = 0
for row in reader:
    # Save header row.
    if rownum > 0:
        ofile.write('TY  - INPR\n')

        # index
        index = 'ID  - springer:{0}'.format(rownum)
        ofile.write(index)
        ofile.write('\n')

        # title
        ofile.write('TI  - ')
        ofile.write(row[0])
        ofile.write('\n')

        # book title
        ofile.write('T2  - ')
        ofile.write(row[1])
        ofile.write('\n')

        # DOI
        ofile.write('DO  - ')
        ofile.write(row[5])
        ofile.write('\n')

        # Author
        ofile.write('AU  - ')
        ofile.write(row[6])
        ofile.write('\n')

        # Year
        ofile.write('PY  - ')
        ofile.write(row[7])
        ofile.write('\n')

        # URL
        ofile.write('UR  - ')
        ofile.write(row[8])
        ofile.write('\n')

        ofile.write('PB  - Springer Berlin Heidelberg\n')
        ofile.write('ER  - \n\n')
    rownum += 1

ifile.close()
ofile.close()
