(ns overscore.main
  (:gen-class)
  (:use overscore.musicxml
        overscore.generator
        overscore.tools.audiveris
        overscore.preprocessing.preprocessing
        overscore.staffline.staffline
        overscore.recognition.segmentation.segmentation
        overscore.recognition.classification.classification
        overscore.semantics.semantics))

(defn usage []
  (println
   "Possible arguments:
    convert <in> <out>
        Convert symbols from Audiveris training set to png
        images in the <out> directory (from the <in> directory)

    preprocessing <in> <out> <out-ref>
        Preprocess the image <in>, saving the output image to
        <out> and the reference lengths descriptions in
        <out-ref>

    staffline <in>
        Isolate the systems and find the staffline positions
        on each system, from the image <in>. Saves the output
        for each system to <in>-n.png and <in>-n.txt, where n
        is an integer

    segmentation <in-img> <in-refs> <out-segs>
        Segment the image <in-img>, with reference lengths
        described in <in-refs>, isolating each symbol. Save
        the segments descriptions in <out-segs>

    classification <training-set> <in-img> <in-segs> <out-classes>
        Classify the segments of <in-img> described by the
        file <in-segs> (generated by the segmentation step),
        using the training set at location <training-set>
        (created by the convert step). Save the segments along
        with their classes in <out-classes>

    semantics <in-classes> <in-refs> <in-stafflines> <out-xml>

        Convert the recognized score to a MusicXML
        document (<out-xml>), from the classes (<in-classes>) output
        by the classification step, the reference lengths (<in-refs>)
        output by the preprocessing step, and the staff lines
        positions (<in-stafflines>, the .txt file) output by the
        staffline step.

    generate <in> <out> <name>
        parse the <in> MusicXML file, and generate the song
        <name> in the clojure file <out>

    play <file> <name>
        play the song called <name> defined in the file <file>"))

(defn call-if
  "Apply the last argument to the first one if its length is equal to
  n. Else, print the usage of this program."
  [args n f]
  (if (= (count args) n)
    (apply f args)
    (usage)))

(defn generate [in out name]
  (write-to-file
   (parse-musicxml in) (symbol name) out)
  (println "Generated song" name "in file" out))

(defn play-song [file name]
  (println "Not implemented yet"))

(defn -main [& args]
  (case (first args)
    "convert"
    (call-if (rest args) 2 convert)
    "generate"
    (call-if (rest args) 3 generate)
    "play"
    (call-if (rest args) 2 play-song)
    "preprocessing"
    (call-if (rest args) 3 preprocessing)
    "staffline"
    (call-if (rest args) 1 staffline-processing)
    "segmentation"
    (call-if (rest args) 3 segmentation)
    "classification"
    (call-if (rest args) 4 classification)
    "semantics"
    (call-if (rest args) 4 semantics)
    (usage)))
