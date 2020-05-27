// @flow
const ester = require('../../main/js/fester');

test('two plus two is four', () => {
    expect(ester.fester(2,2)).toBe("Yo  from glurm4");

});

test('next test', () => {
    expect(ester.bester(4,9)).toBe("Howdy -5");
});