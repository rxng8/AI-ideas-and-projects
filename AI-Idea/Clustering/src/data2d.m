% This script will read output data from a given file and cluster plot it in
%   2 dimensions.
function data2d(filename)
  if (is_octave)
    data = load(filename);
    graphics_toolkit('fltk');
    scatter(data(:, 1), data(:, 2), [], [], '+');
    axis([0, 1, 0, 1]); 
    printf('Press Enter to continue.\n');
    pause
  else
    data = load(filename);
    scatter(data(:, 1), data(:, 2), '+');
    axis([0, 1, 0, 1]); 
    fprintf('Press Enter to continue.\n');
    pause
  end
end

% subfunction that checks if we are in octave
function r = is_octave ()
  persistent x;
  if (isempty (x))
    x = exist ('OCTAVE_VERSION', 'builtin');
  end
  r = x;
end
