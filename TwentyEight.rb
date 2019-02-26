#!/usr/bin/ruby

def send(receiver, message)
    receiver.instance_variable_get("@_queue").push(message)
end

class DataStorageManager
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
        elsif message[0] == "send_word_freqs"
            return _process_words(message[1..-1])
        else
            # send(@_word_freqs_manager, message)
        end

    end
 
    def _init(message)
        # puts "3\n"
        path_to_file = message[0]
        @_stop_word_manager = message[1]
        File.open(path_to_file, "r") do |f|
            f.each_line do |line|
                @_data += line
            end
        end
        @_data = @_data.gsub(/[\W_]+/, ' ').downcase
    end

    def _process_words(message)
        # puts  "4\n"
        recipient = message[0]
        @_words = @_data.split(' ')
        for @_w in @_words
            send(@_stop_word_manager, ["filter", @_w])
        end
        send(@_stop_word_manager, ["top25", recipient])
    end

end

class StopWordManager
    def initialize
        @_queue = Queue.new
        @_stop = false
        @_stop_words = []
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
        elsif message[0] == "filter"
            return _filter(message[1..-1])
        else
            send(@_word_freq_manager, message)
        end

    end
 
    def _init(message)
        # puts "1\n"
        @_word_freq_manager = message[0]
        @_data = ""
        File.open("../stop_words.txt", "r") do |f|
            f.each_line do |line|
                @_data += line
            end
        end
        @_stop_words = @_data.split(',')
        @_stop_words.concat( ["s"] )
    end

    def _filter(message)
        # puts "A\n"
        @_word = message[0]
        if not @_stop_words.include? @_word
            send(@_word_freq_manager, ["word", @_word])
        end
    end

end

class WordFrequencyManager
    def initialize
        @_queue = Queue.new
        @_stop = false
        @_word_freqs = {}
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
        elsif message[0] == "word"
            return _increment_count(message[1..-1])
        elsif message[0] == "top25"
            _top25(message[1..-1])
        end
    
    end
            
    def _init(message)
        # puts "2\n"
        @_storage_manager = message[0]
        @_data = ""
        File.open("../stop_words.txt", "r") do |f|
            f.each_line do |line|
                @_data += line
            end
        end
        send(@_storage_manager, ["send_word_freqs", self])
    end
 
    def _increment_count(message)
        # puts "B\n"
        @_word = message[0]
        if @_word_freqs.has_key?(@_word)
            @_word_freqs[@_word] += 1 
        else
            @_word_freqs[@_word] = 1
        end
    end

    def _top25(message)
        # puts "5\n"
        recipient = message[0]
        @_freqs_sorted = @_word_freqs.sort_by {|_key, value| -value}.to_h.first(25)
        @_freqs_sorted.each do |key, value|
            puts "#{key}  -  #{value}"
        end
        send(@_storage_manager, ["die"])
        @_stop = true
    end

end

threads = []

word_freq_manager = WordFrequencyManager.new
stop_word_manager = StopWordManager.new
storage_manager = DataStorageManager.new

send(stop_word_manager, ["init", word_freq_manager])
send(storage_manager, ["init", ARGV[0], stop_word_manager])
send(word_freq_manager, ["init", storage_manager])

threads << Thread.new { word_freq_manager.run }
threads << Thread.new { stop_word_manager.run }
threads << Thread.new { storage_manager.run }

threads.each { |t| t.join 10 }
