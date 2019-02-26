#!/usr/bin/ruby

def send(receiver, message)
    receiver.instance_variable_get("@_queue").push(message)
end

class Characters
    def initialize
        @_queue = Queue.new
        @_stop = false
        @_data = ""
    end

    def run
        while !@_stop
            message = @_queue.pop
            _dispatch(message)
            if message[0] == "die"
                @_stop = true
            end
        end
    end
    
    def _dispatch(message)
        if message[0] == "init"
            _init(message[1..-1])
        elsif message[0] == "do_work"
            _do_work(message[1..-1])
        end
    end
    
    def _init(message)
        # puts "4\n"
        @_all_words = message[0]
    end
    
    def _do_work(message)
        @_filename = message[0]
        @_data = ""
        File.open(@_filename, "r") do |f|
            f.each_line do |line|
                @_data += line
            end
        end
        
        send(@_all_words, ["feedback", @_data])
    end

end

class AllWords
    def initialize
        @_queue = Queue.new
        @_stop = false
        @_words = []
    end

    def run
        while !@_stop
            message = @_queue.pop
            _dispatch(message)
            if message[0] == "die"
                @_stop = true
            end
        end
    end
    
    def _dispatch(message)
        if message[0] == "init"
            _init(message[1..-1])
        elsif message[0] == "do_work"
            _do_work(message[1..-1])
        elsif message[0] == "feedback"
            _feedback(message[1..-1])
        end
    end
    
    def _init(message)
        # puts "3\n"
        @_characters = message[0]
        @_non_stop_words = message[1]
    end
    
    def _do_work(message)
        send(@_characters, ["do_work", message[0]])
    end
    
    def _feedback(message)
        @_data = message[0]
        @_words = @_data.gsub(/[\W_]+/, ' ').downcase.split(' ')
        send(@_non_stop_words, ["feedback", @_words])
    end

end

class NonStopWords
    def initialize
        @_queue = Queue.new
        @_stop = false
        @_non_stop_words = []
    end

    def run
        while !@_stop
            message = @_queue.pop
            _dispatch(message)
             if message[0] == "die"
                 @_stop = true
             end
        end
    end
    
    def _dispatch(message)
        if message[0] == "init"
            _init(message[1..-1])
        elsif message[0] == "do_work"
            _do_work(message[1..-1])
        elsif message[0] == "feedback"
            _feedback(message[1..-1])
        end
    end
    
    def _init(message)
        # puts "2\n"
        @_all_words = message[0]
        @_count_and_sort = message[1]
    end
    
    def _do_work(message)
        send(@_all_words, ["do_work", message[0]])
    end
    
    def _feedback(message)
        # collect stop_words
        @_data = ""
        File.open("../stop_words.txt", "r") do |f|
            f.each_line do |line|
                @_data += line
            end
        end
        @_stop_words = @_data.split(',')
        @_stop_words.concat( ["s"] )
        
        # remove stop_words from words
        @_words = message[0]
        @_words.each do |word|
            if not @_stop_words.include? word
                @_non_stop_words.push(word)
            end
        end
        
        send(@_count_and_sort, ["feedback", @_non_stop_words])
        
    end

end

class CountAndSort
    def initialize
        @_queue = Queue.new
        @_stop = false
        @_freqs = {}
    end

    def run
        while !@_stop
            message = @_queue.pop
            _dispatch(message)
            if message[0] == "die"
                @_stop = true
            end
        end
    end
    
    def _dispatch(message)
        if message[0] == "init"
            _init(message[1..-1])
        elsif message[0] == "do_work"
            _do_work(message[1..-1])
        elsif message[0] == "feedback"
            _feedback(message[1..-1])
        end
    end
    
    def _init(message)
        # puts "1\n"
        @_non_stop_words = message[0]
    end
    
    def _do_work(message)
        send(@_non_stop_words, ["do_work", message[0]])
    end
    
    def _feedback(message)
    
        # count
        @_non_stop_words = message[0]
        @_non_stop_words.each do |word|
            if @_freqs.has_key?(word)
                @_freqs[word] += 1
            else
                @_freqs[word] = 1
            end
        end
        
        # sort
        @_freqs_sorted = @_freqs.sort_by {|_key, value| -value}.to_h.first(25)
        @_freqs_sorted.each do |key, value|
            puts "#{key}  -  #{value}"
        end
        
    end

end

threads = []

characters = Characters.new
all_words = AllWords.new
non_stop_words = NonStopWords.new
count_and_sort = CountAndSort.new

send(characters, ["init", all_words])
send(all_words, ["init", characters, non_stop_words])
send(non_stop_words, ["init", all_words, count_and_sort])
send(count_and_sort, ["init", non_stop_words])

threads << Thread.new { characters.run }
threads << Thread.new { all_words.run }
threads << Thread.new { non_stop_words.run }
threads << Thread.new { count_and_sort.run }

send(count_and_sort, ["do_work", ARGV[0]])

threads.each { |t| t.join 10 }
