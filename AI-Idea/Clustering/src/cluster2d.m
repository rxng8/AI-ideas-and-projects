% This script will read output data from a given file and cluster plot it in
%   2 dimensions.
function cluster2d(filename)
  if (is_octave)
    data = load(filename);
    graphics_toolkit('fltk');
    numClusters = max(data(:, 1)) + 1;
    scatter(data(numClusters+1:end, 2), data(numClusters+1:end, 3), [], data(numClusters+1:end, 1), '+');
    hold on;
    scatter(data(1:numClusters, 2), data(1:numClusters, 3), [], data (1:numClusters, 1), 's', 'filled');
    axis([0, 1, 0, 1]); 
    hold off;
    printf('Press Enter to continue.\n');
    pause
  else
    data = load(filename, ' ');
    numClusters = max(data(:, 1)) + 1;
    fprintf('%d\n', numClusters);
    scatter(data(numClusters+1:end, 2), data(numClusters+1:end, 3), [], data(numClusters+1:end, 1), '+');
    hold on;
    scatter(data(1:numClusters, 2), data(1:numClusters, 3), [], data(1:numClusters, 1), 's', 'filled');
    axis([0, 1, 0, 1]);
    hold off;
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
